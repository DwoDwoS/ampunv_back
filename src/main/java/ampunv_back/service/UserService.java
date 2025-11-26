package ampunv_back.service;

import ampunv_back.dto.RegisterRequest;
import ampunv_back.dto.UserDTO;
import ampunv_back.entity.City;
import ampunv_back.entity.User;
import ampunv_back.repository.CityRepository;
import ampunv_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final long ORIGINAL_ADMIN_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable avec l'ID : " + request.getCityId()));

        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCity(city);
        user.setRole(User.UserRole.SELLER);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void promoteToAdmin(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getId() == ORIGINAL_ADMIN_ID) {
            throw new IllegalStateException("L'administrateur originel ne peut pas être modifié");
        }

        user.setRole(User.UserRole.ADMIN);
        userRepository.save(user);
    }

    public void demoteToSeller(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getId() == ORIGINAL_ADMIN_ID) {
            throw new IllegalStateException("L'administrateur originel ne peut pas être rétrogradé");
        }

        if (user.getRole() != User.UserRole.ADMIN) {
            throw new IllegalStateException("Cet utilisateur n'est pas un administrateur");
        }

        user.setRole(User.UserRole.SELLER);
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getCityId(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getId() == ORIGINAL_ADMIN_ID
        );
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getId() == ORIGINAL_ADMIN_ID) {
            throw new IllegalStateException("L'administrateur originel ne peut pas être supprimé");
        }

        userRepository.delete(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}