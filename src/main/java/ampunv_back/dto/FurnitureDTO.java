package ampunv_back.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FurnitureDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer furnitureTypeId;
    private String furnitureTypeName;
    private Integer materialId;
    private Integer colorId;
    private Integer cityId;
    private String condition;
    private String status;
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ImageDTO> images;
    private String rejectionReason;

    public FurnitureDTO() {
    }

    public FurnitureDTO(Long id, String title, String description, BigDecimal price,
                        Integer furnitureTypeId, String furnitureTypeName,
                        Integer materialId, Integer colorId, Integer cityId,
                        String condition, String status, Long sellerId, String sellerName,
                        LocalDateTime createdAt, LocalDateTime updatedAt, List<ImageDTO> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.furnitureTypeId = furnitureTypeId;
        this.furnitureTypeName = furnitureTypeName;
        this.materialId = materialId;
        this.colorId = colorId;
        this.cityId = cityId;
        this.condition = condition;
        this.status = status;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
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

    public String getFurnitureTypeName() {
        return furnitureTypeName;
    }

    public void setFurnitureTypeName(String furnitureTypeName) {
        this.furnitureTypeName = furnitureTypeName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public static class ImageDTO {
        private Long id;
        private String url;
        private String name;
        private Boolean isPrimary;

        public ImageDTO() {
        }

        public ImageDTO(Long id, String url, String name, Boolean isPrimary) {
            this.id = id;
            this.url = url;
            this.name = name;
            this.isPrimary = isPrimary;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }
    }
}