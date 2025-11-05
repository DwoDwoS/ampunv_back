package ampunv_back.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.encrypt.Encryptors;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String email;

    @Column(name = "phone_encrypted")
    private String phoneEncrypted;

    @Transient
    private String phone;

    @PrePersist
    @PreUpdate
    private void encryptSensitiveData() {
        if (phone != null) {
            this.phoneEncrypted = encryptData(phone);
        }
    }

    @PostLoad
    private void decryptSensitiveData() {
        if (phoneEncrypted != null) {
            this.phone = decryptData(phoneEncrypted);
        }
    }

    private String encryptData(String data) {
        return "encrypted_" + data;
    }

    private String decryptData(String encrypted) {
        return encrypted.replace("encrypted_", "");  // Simplification
    }

}