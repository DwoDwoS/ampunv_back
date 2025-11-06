package ampunv_back.controller;

import ampunv_back.dto.CreateFurnitureRequest;
import ampunv_back.dto.FurnitureDTO;
import ampunv_back.dto.UpdateFurnitureRequest;
import ampunv_back.entity.Furniture;
import ampunv_back.service.FurnitureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/furnitures")
@CrossOrigin(origins = "http://localhost:3000")
public class FurnitureController {

    @Autowired
    private FurnitureService furnitureService;

    @GetMapping
    public ResponseEntity<List<FurnitureDTO>> getAllFurnitures() {
        List<FurnitureDTO> furnitures = furnitureService.getAllAvailableFurnitures();
        return ResponseEntity.ok(furnitures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FurnitureDTO> getFurnitureById(@PathVariable Long id) {
        Furniture furniture = furnitureService.findById(id);
        FurnitureDTO dto = furnitureService.convertToDTO(furniture);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> createFurniture(
            @Valid @RequestBody CreateFurnitureRequest request,
            Authentication authentication) {
        try {
            String sellerEmail = authentication.getName();
            Furniture furniture = furnitureService.createFurniture(request, sellerEmail);
            FurnitureDTO dto = furnitureService.convertToDTO(furniture);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-furnitures")
    public ResponseEntity<List<FurnitureDTO>> getMyFurnitures(Authentication authentication) {
        String sellerEmail = authentication.getName();
        List<FurnitureDTO> furnitures = furnitureService.getMyFurnitures(sellerEmail);
        return ResponseEntity.ok(furnitures);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFurniture(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFurnitureRequest request,
            Authentication authentication) {
        try {
            String sellerEmail = authentication.getName();
            Furniture furniture = furnitureService.updateFurniture(id, request, sellerEmail);
            FurnitureDTO dto = furnitureService.convertToDTO(furniture);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFurniture(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String sellerEmail = authentication.getName();
            furnitureService.deleteFurniture(id, sellerEmail);
            return ResponseEntity.ok("Meuble supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<FurnitureDTO>> searchFurnitures(@RequestParam String keyword) {
        List<FurnitureDTO> furnitures = furnitureService.searchFurnitures(keyword);
        return ResponseEntity.ok(furnitures);
    }
}