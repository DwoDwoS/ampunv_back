package ampunv_back.service;

import ampunv_back.entity.Furniture;
import ampunv_back.entity.Image;
import ampunv_back.repository.FurnitureRepository;
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

    @Autowired
    private FurnitureRepository furnitureRepository;

    @Value("${cloudinary.folder}")
    private String folder;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MAX_IMAGES_PER_FURNITURE = 5;
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    public Image uploadImage(MultipartFile file, Long furnitureId, String altText, String sellerEmail) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new IllegalArgumentException("Meuble non trouvé"));

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à uploader des images pour ce meuble");
        }

        Map uploadResult;
            uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "image"
                        )
                );

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");
        Integer width = (Integer) uploadResult.get("width");
        Integer height = (Integer) uploadResult.get("height");
        Long bytes = ((Number) uploadResult.get("bytes")).longValue();
        long imageCount = imageRepository.countByFurnitureId(furnitureId);
        boolean isPrimary = (imageCount == 0);

        Image image = new Image();
        image.setUrl(url);
        image.setPublicId(publicId);
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

    public void setPrimaryImage(Long imageId, Long furnitureId, String sellerEmail) {
        Furniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new IllegalArgumentException("Meuble non trouvé"));

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce meuble");
        }

        List<Image> images = imageRepository.findByFurnitureIdOrderByDisplayOrderAsc(furnitureId);
        images.forEach(img -> {
            img.setIsPrimary(false);
            imageRepository.save(img);
        });

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        if (!image.getFurnitureId().equals(furnitureId)) {
            throw new IllegalArgumentException("Cette image n'appartient pas à ce meuble");
        }

        image.setIsPrimary(true);
        imageRepository.save(image);
    }

    public void deleteImage(Long imageId, String sellerEmail) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        Furniture furniture = furnitureRepository.findById(image.getFurnitureId())
                .orElseThrow(() -> new IllegalArgumentException("Meuble non trouvé"));

        if (!furniture.getSeller().getEmail().equals(sellerEmail)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer cette image");
        }
        try {
            cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Erreur Cloudinary lors de la suppression : " + e.getMessage());
        }
        imageRepository.delete(image);
    }
}