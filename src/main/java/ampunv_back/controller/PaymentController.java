package ampunv_back.controller;

import ampunv_back.entity.User;
import ampunv_back.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://ampunv.vercel.app", "https://stripe.com"})
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final StripeService stripeService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(
            @RequestParam Long furnitureId,
            @RequestParam(required = false) String buyerEmail,
            Authentication authentication
    ) {
        try {
            User buyer = null;

            if (authentication != null && authentication.getPrincipal() instanceof User) {
                buyer = (User) authentication.getPrincipal();
            } else if (buyerEmail != null && !buyerEmail.isEmpty()) {
                buyer = new User();
                buyer.setEmail(buyerEmail);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email requis pour un achat en tant qu'invité."));
            }

            Map<String, String> response = stripeService.createPaymentIntent(furnitureId, buyer);
            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            log.error("Stripe error during create-intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne: " + e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        log.info("Stripe event received: {}", event.getType());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        switch (event.getType()) {
            case "payment_intent.succeeded": {
                PaymentIntent paymentIntent = (PaymentIntent) deserializer.getObject().orElse(null);
                if (paymentIntent == null) {
                    log.error("Unable to deserialize PaymentIntent for succeeded event");
                    break;
                }
                stripeService.handlePaymentSuccess(paymentIntent.getId());
                break;
            }

            case "payment_intent.payment_failed": {
                PaymentIntent failedIntent = (PaymentIntent) deserializer.getObject().orElse(null);
                if (failedIntent == null) {
                    log.error("Unable to deserialize PaymentIntent for failed event");
                    break;
                }
                stripeService.handlePaymentFailed(failedIntent.getId());
                break;
            }

            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Received");
    }
}