package ampunv_back.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class UpdateFurnitureRequest {

    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999999.99")
    private BigDecimal price;

    private Integer furnitureTypeId;

    private Integer materialId;

    private Integer colorId;

    private Integer cityId;

    @Pattern(regexp = "^(Neuf|Très bon état|Bon état|État correct|À restaurer)$",
            message = "État invalide")
    private String condition;

    @Pattern(regexp = "^(AVAILABLE|RESERVED|SOLD)$", message = "Statut invalide")
    private String status;

    public UpdateFurnitureRequest() {}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}