package ampunv_back.repository;

import ampunv_back.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByFurnitureIdOrderByDisplayOrderAsc(Long furnitureId);
    Optional<Image> findByFurnitureIdAndIsPrimaryTrue(Long furnitureId);
    long countByFurnitureId(Long furnitureId);
    void deleteByFurnitureId(Long furnitureId);
}