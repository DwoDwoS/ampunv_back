package ampunv_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "furniture_rejection_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "furniture_id", nullable = false)
    private Long furnitureId;

    @Column(name = "furniture_title")
    private String furnitureTitle;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "rejected_at", updatable = false)
    private LocalDateTime rejectedAt;
}