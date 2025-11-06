package ampunv_back.repository;

import ampunv_back.entity.FurnitureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FurnitureTypeRepository extends JpaRepository<FurnitureType, Integer> {
    Optional<FurnitureType> findByName(String name);
    boolean existsByName(String name);
}