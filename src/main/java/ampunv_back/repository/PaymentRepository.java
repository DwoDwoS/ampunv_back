package ampunv_back.repository;

import ampunv_back.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    List<Payment> findByBuyerId(Long buyerId);
    List<Payment> findBySellerId(Long sellerId);
    List<Payment> findByFurnitureId(Long furnitureId);
}