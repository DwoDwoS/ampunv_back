package ampunv_back.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "furnitures")
public class Furniture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "furniture_type_id", nullable = false)
    private Integer furnitureTypeId;

    @Column(name = "material_id")
    private Integer materialId;

    @Column(name = "color_id")
    private Integer colorId;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(nullable = false)
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FurnitureStatus status = FurnitureStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Furniture() {}

    public Furniture(String title, String description, BigDecimal price, User seller) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.seller = seller;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getFurnitureTypeId() {
        return furnitureTypeId;
    }

    public void setFurnitureTypeId(Integer furnitureTypeId) {
        this.furnitureTypeId = furnitureTypeId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public Integer getColorId() {
        return colorId;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public FurnitureStatus getStatus() {
        return status;
    }

    public void setStatus(FurnitureStatus status) {
        this.status = status;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Long getSellerId() {
        return seller != null ? seller.getId() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum FurnitureStatus {
        AVAILABLE,
        RESERVED,
        SOLD
    }
}