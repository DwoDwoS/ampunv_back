package ampunv_back.controller;

import ampunv_back.dto.PublicUserDTO;
import ampunv_back.entity.User;
import ampunv_back.service.DataMaskingService;
import ampunv_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataMaskingService maskingService;

    @GetMapping("/{id}/public")
    public ResponseEntity<PublicUserDTO> getPublicProfile(@PathVariable Long id) {
        User user = userService.findById(id);

        PublicUserDTO dto = new PublicUserDTO();
        dto.setId(user.getId());
        dto.setDisplayName(maskingService.getDisplayName(user.getFirstname(), user.getLastname()));
        dto.setCityName(user.getCity().getName());
        dto.setMemberSince(user.getCreatedAt().getYear() + "");

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long id) {
        User user = userService.findById(id);

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());

        return ResponseEntity.ok(dto);
    }
}