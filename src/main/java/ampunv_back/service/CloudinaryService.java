package ampunv_back.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "ampunv/furnitures",
                        "public_id", "furniture_" + UUID.randomUUID().toString(),
                        "resource_type", "image",
                        "transformation", new com.cloudinary.Transformation()
                                .width(1200)
                                .height(1200)
                                .crop("limit")
                                .quality("auto")
                                .fetchFormat("auto")
                )
        );

        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                path = path.replaceFirst("v\\d+/", "");
                return path.replaceFirst("\\.[^.]+$", "");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction du public_id: " + e.getMessage());
        }
        return null;
    }
}