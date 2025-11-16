package ampunv_back.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendApprovalEmail(String email, String furnitureTitle) {
        System.out.println("Email envoyé à " + email + " : Meuble '" + furnitureTitle + "' approuvé !");
    }

    public void sendRejectionEmail(String email, String furnitureTitle, String reason) {
        System.out.println("Email envoyé à " + email + " : Meuble '" + furnitureTitle + "' rejeté. Raison : " + reason);
    }
}