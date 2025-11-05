package ampunv_back.dto;

public class ImageUploadResponse {
    private Long id;
    private String url;
    private String name;
    private Boolean isPrimary;
    private String message;

    public ImageUploadResponse(Long id, String url, String name, Boolean isPrimary, String message) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.isPrimary = isPrimary;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}