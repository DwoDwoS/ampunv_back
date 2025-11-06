package ampunv_back.service;

import org.springframework.stereotype.Service;

@Service
public class DataMaskingService {

    public String maskLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return "";
        }
        return lastName.charAt(0) + ".";
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 1) {
            return "*@" + domain;
        }

        return localPart.charAt(0) + "***@" + domain;
    }

    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "** ** ** **";
        }

        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.length() < 10) {
            return "** ** ** **";
        }

        return cleaned.substring(0, 2) + " ** ** ** " + cleaned.substring(8);
    }

    public String maskAddress(String fullAddress, String city) {
        return city;
    }

    public String getDisplayName(String firstname, String lastname) {
        return firstname + " " + maskLastName(lastname);
    }
}