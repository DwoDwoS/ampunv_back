package ampunv_back.controller;

import ampunv_back.dto.FurnitureDTO;
import ampunv_back.dto.UpdateFurnitureRequest;
import ampunv_back.dto.UserDTO;
import ampunv_back.entity.Furniture;
import ampunv_back.entity.User;
import ampunv_back.service.UserService;
import ampunv_back.service.FurnitureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private FurnitureService furnitureService;

    @PostMapping("/users/{userId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long userId) {
        try {
            userService.promoteToAdmin(userId);
            return ResponseEntity.ok("Utilisateur promu admin avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> demoteUserToSeller(@PathVariable Long userId) {
        try {
            userService.demoteToSeller(userId);
            return ResponseEntity.ok("Utilisateur rétrogradé en SELLER");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/furnitures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFurnitureStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFurnitureRequest request) {
        try {
            Furniture furniture = furnitureService.updateFurnitureAsAdmin(id, request);
            FurnitureDTO dto = furnitureService.convertToDTO(furniture);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/furnitures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFurniture(@PathVariable Long id) {
        try {
            furnitureService.deleteFurnitureAsAdmin(id);
            return ResponseEntity.ok("Meuble supprimé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/furnitures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FurnitureDTO>> getAllFurnitures() {
        return ResponseEntity.ok(furnitureService.getAllFurnitures());
    }
}