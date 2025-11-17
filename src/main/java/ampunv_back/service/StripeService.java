package ampunv_back.service;

import ampunv_back.entity.Furniture;
import ampunv_back.entity.Payment;
import ampunv_back.entity.User;
import ampunv_back.repository.FurnitureRepository;
import ampunv_back.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PaymentRepository paymentRepository;
    private final FurnitureRepository furnitureRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Map<String, String> createPaymentIntent(Long furnitureId, User buyer) throws StripeException {
        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        long amountInCents = furniture.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("eur")
                .putMetadata("furniture_id", furnitureId.toString())
                .putMetadata("seller_id", furniture.getSeller().getId().toString());

        if (buyer.getId() != null) {
            paramsBuilder.putMetadata("buyer_id", buyer.getId().toString());
        } else {
            paramsBuilder.putMetadata("guest_purchase", "true");
        }

        if (buyer.getEmail() != null && !buyer.getEmail().isEmpty()) {
            paramsBuilder.setReceiptEmail(buyer.getEmail());
        }
        
        PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

        if (buyer.getId() != null) {
            Payment payment = new Payment();
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setFurniture(furniture);
            payment.setBuyer(buyer);
            payment.setSeller(furniture.getSeller());
            payment.setAmount(furniture.getPrice());
            payment.setCurrency("EUR");
            payment.setStatus("pending");
            paymentRepository.save(payment);
        }

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        return response;
    }

    @Transactional
    public void handlePaymentSuccess(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElse(null);

        if (payment != null) {
            payment.setStatus("succeeded");
            paymentRepository.save(payment);

            Furniture furniture = payment.getFurniture();
            furniture.setStatus(Furniture.FurnitureStatus.SOLD);
            furnitureRepository.save(furniture);
        } else {
            try {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                Map<String, String> metadata = paymentIntent.getMetadata();

                Long furnitureId = Long.parseLong(metadata.get("furniture_id"));
                Furniture furniture = furnitureRepository.findById(furnitureId)
                        .orElseThrow(() -> new RuntimeException("Furniture not found"));

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

            } catch (StripeException e) {
                throw new RuntimeException("Error retrieving payment intent: " + e.getMessage());
            }
        }
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("failed");
        paymentRepository.save(payment);
    }
}