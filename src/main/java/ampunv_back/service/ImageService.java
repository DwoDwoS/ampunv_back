package ampunv_back.service;

import ampunv_back.entity.Image;
import ampunv_back.repository.ImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${cloudinary.folder}")
    private String folder;

    public Image uploadImage(MultipartFile file, Long furnitureId, String altText) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "image",
                        "transformation", ObjectUtils.asMap(
                                "quality", "auto",
                                "fetch_format", "auto"
                        )
                )
        );

        String url = (String) uploadResult.get("secure_url");
        Integer width = (Integer) uploadResult.get("width");
        Integer height = (Integer) uploadResult.get("height");
        Long bytes = ((Number) uploadResult.get("bytes")).longValue();
        long imageCount = imageRepository.countByFurnitureId(furnitureId);
        boolean isPrimary = (imageCount == 0);

        Image image = new Image();
        image.setUrl(url);
        image.setName(file.getOriginalFilename());
        image.setAltText(altText);
        image.setFurnitureId(furnitureId);
        image.setMimeType(contentType);
        image.setSizeBytes(bytes);
        image.setWidthPx(width);
        image.setHeightPx(height);
        image.setDisplayOrder((int) imageCount);
        image.setIsPrimary(isPrimary);

        return imageRepository.save(image);
    }

    public List<Image> getImagesByFurnitureId(Long furnitureId) {
        return imageRepository.findByFurnitureIdOrderByDisplayOrderAsc(furnitureId);
    }

    public Image getPrimaryImage(Long furnitureId) {
        return imageRepository.findByFurnitureIdAndIsPrimaryTrue(furnitureId)
                .orElse(null);
    }

    public void setPrimaryImage(Long imageId, Long furnitureId) {
        List<Image> images = imageRepository.findByFurnitureIdOrderByDisplayOrderAsc(furnitureId);
        images.forEach(img -> {
            img.setIsPrimary(false);
            imageRepository.save(img);
        });

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        image.setIsPrimary(true);
        imageRepository.save(image);
    }

    public void deleteImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));
        String url = image.getUrl();
        String publicId = extractPublicIdFromUrl(url);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        imageRepository.delete(image);
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/upload/");
        if (parts.length == 2) {
            String afterUpload = parts[1];
            String withoutVersion = afterUpload.substring(afterUpload.indexOf("/") + 1);
            return withoutVersion.substring(0, withoutVersion.lastIndexOf("."));
        }
        throw new IllegalArgumentException("URL Cloudinary invalide");
    }
}