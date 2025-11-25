package ampunv_back.repository;

import ampunv_back.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.furniture.id = :furnitureId")
    int deleteByFurnitureId(@Param("furnitureId") Long furnitureId);
}