package com.finance.investment.Service;


import com.finance.investment.model.User;
import com.finance.investment.Repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    public MfaService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    public String generateMfaSetupQrUri(User user) {
        // Generate a new secret key for the user
        String secret = googleAuthenticator.createCredentials().getKey();

        // Save the secret to the user entity
        user.setMfaSecret(secret);
        userRepository.save(user);

        // Build the URI for the QR code, which authenticator apps can scan
        return "otpauth://totp/MyProject:" + user.getUsername() +
                "?secret=" + secret + "&issuer=MyProject";
    }

    public boolean verifyTotpCode(String username, int code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return googleAuthenticator.authorize(user.getMfaSecret(), code);
    }
}
