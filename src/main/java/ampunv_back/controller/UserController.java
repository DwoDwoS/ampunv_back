package ampunv_back.controller;

import ampunv_back.dto.PublicUserDTO;
import ampunv_back.dto.UpdatePasswordRequest;
import ampunv_back.dto.UserDTO;
import ampunv_back.entity.User;
import ampunv_back.service.DataMaskingService;
import ampunv_back.service.FurnitureService;
import ampunv_back.service.UserService;
import ampunv_back.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://ampunv.vercel.app/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataMaskingService maskingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FurnitureService furnitureService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}/public")
    public ResponseEntity<PublicUserDTO> getPublicProfile(@PathVariable Long id) {

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PublicUserDTO dto = new PublicUserDTO();
        dto.setId(user.getId());
        dto.setDisplayName(maskingService.getDisplayName(user.getFirstname(), user.getLastname()));
        dto.setCityName(user.getCity().getName());
        dto.setMemberSince(String.valueOf(user.getCreatedAt().getYear()));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/myprofile")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @PutMapping("/myprofile")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        try {
            String currentEmail = authentication.getName();

            User user = userService.findByEmail(currentEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!request.getEmail().equals(currentEmail)) {
                if (userService.findByEmail(request.getEmail()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Cet email est déjà utilisé"));
                }
            }

            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            user.setEmail(request.getEmail());
            user.setCityId(request.getCityId());

            return ResponseEntity.ok(userService.save(user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/myprofile/password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication
    ) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Mot de passe actuel incorrect"));
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.save(user);

            return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/myprofile")
    @Transactional
    public ResponseEntity<?> deleteMyAccount(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            furnitureService.deleteAllByUser(user);
            userService.deleteUser(user.getId());

            return ResponseEntity.ok(Map.of("message", "Compte supprimé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Seulement les admins peuvent supprimer
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long id) {

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userService.convertToDTO(user));
    }
}