package ampunv_back.controller;

import ampunv_back.dto.AuthResponse;
import ampunv_back.dto.LoginRequest;
import ampunv_back.dto.RegisterRequest;
import ampunv_back.entity.User;
import ampunv_back.service.JwtService;
import ampunv_back.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(request);

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getRole().name()
            );

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getRole().name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userService.findByEmail(request.getEmail());

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getRole().name()
            );

            AuthResponse response = new AuthResponse(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getRole().name()
            );
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la connexion : " + e.getMessage());
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(new EmailCheckResponse(exists));
    }

    private static class EmailCheckResponse {
        private boolean exists;

        public EmailCheckResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
}