package ampunv_back.repository;

import ampunv_back.entity.RejectionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RejectionLogRepository extends JpaRepository<RejectionLog, Long> {
    Optional<RejectionLog> findFirstByFurnitureIdOrderByRejectedAtDesc(Long furnitureId);
}