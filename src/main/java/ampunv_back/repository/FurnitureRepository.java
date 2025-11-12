package ampunv_back.repository;

import ampunv_back.entity.Furniture;
import ampunv_back.entity.Furniture.FurnitureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FurnitureRepository extends JpaRepository<Furniture, Long> {
    List<Furniture> findBySeller_Id(Long sellerId);
    List<Furniture> findByStatus(FurnitureStatus status);
    List<Furniture> findByCityId(Integer cityId);
    List<Furniture> findByFurnitureTypeId(Integer furnitureTypeId);

    @Query("SELECT f FROM Furniture f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Furniture> searchByTitle(@Param("keyword") String keyword);
    List<Furniture> findByStatusAndCityId(FurnitureStatus status, Integer cityId);
    long countBySeller_Id(Long sellerId);
}