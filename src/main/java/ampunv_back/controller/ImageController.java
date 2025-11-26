package ampunv_back.controller;

import ampunv_back.dto.ImageUploadResponse;
import ampunv_back.entity.Image;
import ampunv_back.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "https://ampunv.vercel.app/")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("furniture_id") Long furnitureId,
            @RequestParam(value = "alt_text", required = false) String altText,
            Authentication authentication
    ) {

        try {
            String sellerEmail = authentication.getName();
            Image image = imageService.uploadImage(file, furnitureId, altText, sellerEmail);

            ImageUploadResponse response = new ImageUploadResponse(
                    image.getId(),
                    image.getUrl(),
                    image.getName(),
                    image.getIsPrimary(),
                    "Image uploadée avec succès"
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur inattendue: " + e.getMessage());
        }
    }

    @GetMapping("/furniture/{furnitureId}")
    public ResponseEntity<List<Image>> getImagesByFurniture(@PathVariable Long furnitureId) {
        List<Image> images = imageService.getImagesByFurnitureId(furnitureId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/furniture/{furnitureId}/primary")
    public ResponseEntity<Image> getPrimaryImage(@PathVariable Long furnitureId) {
        Image image = imageService.getPrimaryImage(furnitureId);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<String> setPrimaryImage(
            @PathVariable Long imageId,
            @RequestParam("furniture_id") Long furnitureId,
            Authentication authentication
    ) {
        try {
            String sellerEmail = authentication.getName();
            imageService.setPrimaryImage(imageId, furnitureId, sellerEmail);
            return ResponseEntity.ok("Image définie comme principale");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(
            @PathVariable Long imageId,
            Authentication authentication
    ) {
        try {
            String sellerEmail = authentication.getName();
            imageService.deleteImage(imageId, sellerEmail);
            return ResponseEntity.ok("Image supprimée avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}