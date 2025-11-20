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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

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

        City city = cityRepository.findById((Integer) request.getCityId())
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
        user.setRole(User.UserRole.ADMIN);
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
                user.getUpdatedAt()
        );
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void demoteToSeller(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getRole() == User.UserRole.ADMIN) {
            long adminCount = userRepository.countByRole(User.UserRole.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Impossible de rétrograder le dernier administrateur");
            }
        }

        user.setRole(User.UserRole.SELLER);
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}