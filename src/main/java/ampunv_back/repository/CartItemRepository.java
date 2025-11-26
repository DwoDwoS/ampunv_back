package ampunv_back.repository;

import ampunv_back.entity.CartItem;
import ampunv_back.entity.User;
import ampunv_back.entity.Furniture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndFurniture(User user, Furniture furniture);
    void deleteByUser(User user);
    void deleteByFurnitureId(Long furnitureId);
}
