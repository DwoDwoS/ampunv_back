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

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("eur")
                .putMetadata("furniture_id", furnitureId.toString())
                .putMetadata("buyer_id", buyer.getId().toString())
                .putMetadata("seller_id", furniture.getSeller().getId().toString())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment payment = new Payment();
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setFurniture(furniture);
        payment.setBuyer(buyer);
        payment.setSeller(furniture.getSeller());
        payment.setAmount(furniture.getPrice());
        payment.setCurrency("EUR");
        payment.setStatus("pending");
        paymentRepository.save(payment);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        return response;
    }

    @Transactional
    public void handlePaymentSuccess(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("succeeded");
        paymentRepository.save(payment);

        Furniture furniture = payment.getFurniture();
        furniture.setStatus(Furniture.FurnitureStatus.SOLD);
        furnitureRepository.save(furniture);
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("failed");
        paymentRepository.save(payment);
    }
}