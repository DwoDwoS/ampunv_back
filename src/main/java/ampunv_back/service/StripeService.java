package ampunv_back.service;

import ampunv_back.entity.Furniture;
import ampunv_back.entity.Payment;
import ampunv_back.entity.User;
import ampunv_back.repository.CartItemRepository;
import ampunv_back.repository.FurnitureRepository;
import ampunv_back.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    private final PaymentRepository paymentRepository;
    private final FurnitureRepository furnitureRepository;
    private final CartItemRepository cartItemRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Map<String, String> createPaymentIntent(Long furnitureId, User buyer) throws StripeException {
        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        long amountInCents = furniture.getPrice()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("eur")
                .putMetadata("furniture_id", furnitureId.toString())
                .putMetadata("seller_id", furniture.getSeller().getId().toString());

        if (buyer != null && buyer.getId() != null) {
            paramsBuilder.putMetadata("buyer_id", buyer.getId().toString());
        } else {
            paramsBuilder.putMetadata("guest_purchase", "true");
        }

        if (buyer != null && buyer.getEmail() != null && !buyer.getEmail().isEmpty()) {
            paramsBuilder.setReceiptEmail(buyer.getEmail());
        }

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(paramsBuilder.build());
        } catch (StripeException e) {
            log.error("Stripe PaymentIntent creation failed for furnitureId {}: {}", furnitureId, e.getMessage());
            throw e;
        }

        if (buyer != null && buyer.getId() != null) {
            Optional<Payment> existing = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());
            if (existing.isEmpty()) {
                Payment payment = new Payment();
                payment.setStripePaymentIntentId(paymentIntent.getId());
                payment.setFurniture(furniture);
                payment.setBuyer(buyer);
                payment.setSeller(furniture.getSeller());
                payment.setAmount(furniture.getPrice());
                payment.setCurrency("EUR");
                payment.setStatus("pending");
                paymentRepository.save(payment);
            } else {
                log.info("Payment already exists for PaymentIntent {}", paymentIntent.getId());
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        return response;
    }

    @Transactional
    public void handlePaymentSuccess(String paymentIntentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);

        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();

            if ("succeeded".equalsIgnoreCase(payment.getStatus())) {
                log.info("Payment {} already processed (succeeded). Ignoring duplicate webhook.", paymentIntentId);
                return;
            }

            payment.setStatus("succeeded");
            paymentRepository.save(payment);

            Furniture furniture = payment.getFurniture();
            if (furniture != null) {
                Long fid = furniture.getId();
                furniture.setStatus(Furniture.FurnitureStatus.SOLD);
                furnitureRepository.save(furniture);

                try {
                    cartItemRepository.deleteByFurnitureId(fid);
                } catch (Exception e) {
                    log.warn("Failed to delete cart items for furniture {}: {}", fid, e.getMessage());
                }
            } else {
                log.warn("Payment {} has no associated furniture.", paymentIntentId);
            }

            return;
        }

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Stripe exception while retrieving PaymentIntent {}: {}", paymentIntentId, e.getMessage());
            throw new RuntimeException("Error retrieving payment intent: " + e.getMessage());
        }

        Map<String, String> metadata = paymentIntent.getMetadata();
        if (metadata == null || !metadata.containsKey("furniture_id")) {
            log.error("PaymentIntent {} metadata missing furniture_id. Metadata: {}", paymentIntentId, metadata);
            throw new RuntimeException("Missing furniture_id metadata on PaymentIntent");
        }

        Long furnitureId;
        try {
            furnitureId = Long.parseLong(metadata.get("furniture_id"));
        } catch (NumberFormatException e) {
            log.error("Invalid furniture_id '{}' in metadata for intent {}",
                    metadata.get("furniture_id"), paymentIntentId);
            throw new RuntimeException("Invalid furniture_id metadata");
        }

        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        Optional<Payment> existing = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (existing.isPresent()) {
            log.info("Payment {} already recorded while processing guest flow. Skipping creation.", paymentIntentId);
            return;
        }

        Payment guestPayment = new Payment();
        guestPayment.setStripePaymentIntentId(paymentIntentId);
        guestPayment.setFurniture(furniture);
        guestPayment.setBuyer(null);
        guestPayment.setSeller(furniture.getSeller());
        guestPayment.setAmount(furniture.getPrice());
        guestPayment.setCurrency("EUR");
        guestPayment.setStatus("succeeded");
        paymentRepository.save(guestPayment);

        furniture.setStatus(Furniture.FurnitureStatus.SOLD);
        furnitureRepository.save(furniture);

        try {
            cartItemRepository.deleteByFurnitureId(furnitureId);
        } catch (Exception e) {
            log.warn("Failed to delete cart items for furniture {}: {}", furnitureId, e.getMessage());
        }
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (paymentOpt.isEmpty()) {
            log.warn("Received failed event for unknown PaymentIntent {}", paymentIntentId);
            return;
        }

        Payment payment = paymentOpt.get();

        if ("failed".equalsIgnoreCase(payment.getStatus())) {
            log.info("Payment {} already marked as failed.", paymentIntentId);
            return;
        }
        payment.setStatus("failed");
        paymentRepository.save(payment);
    }
}