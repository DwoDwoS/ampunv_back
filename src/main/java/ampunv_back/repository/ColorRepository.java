package ampunv_back.repository;

import ampunv_back.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    Optional<Color> findByName(String name);
    boolean existsByName(String name);
}