package ampunv_back.controller;

import ampunv_back.dto.UserDTO;
import ampunv_back.entity.User;
import ampunv_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}