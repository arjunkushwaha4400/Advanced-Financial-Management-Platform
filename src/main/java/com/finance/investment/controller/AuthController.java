package com.finance.investment.controller;

import com.finance.investment.Repository.UserRepository;
import com.finance.investment.Service.MfaService;
import com.finance.investment.Service.UserService;
import com.finance.investment.dto.JwtResponseDto;
import com.finance.investment.dto.LoginRequestDto;
import com.finance.investment.dto.UserRegistrationDto;
import com.finance.investment.model.User;
import com.finance.investment.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MfaService mfaService;
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository, MfaService mfaService,UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.mfaService = mfaService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        // Crucial validation: Check if the username already exists using the service layer.
        if (userService.existsByUsername(registrationDto.username())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(registrationDto.username());
        user.setPassword(registrationDto.password());

        User registeredUser = userService.registerNewUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        // Step 1: Authenticate password
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        User user = userService.findByUsername(loginDto.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Check if MFA is enabled
        if (user.isMfaEnabled()) {
            // If MFA is enabled, verify the TOTP code
            if (loginDto.mfaCode() == null || !mfaService.verifyTotpCode(loginDto.username(), loginDto.mfaCode())) {
                // Return an error or a specific status code indicating MFA is required
                return ResponseEntity.badRequest().body("MFA code is invalid or missing.");
            }
        }

        // Step 3: If MFA is not enabled, or if it was enabled and verified, generate JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDto(jwtToken));
    }

    @GetMapping("/mfa/setup")
    public ResponseEntity<String> setupMfa(@AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String qrUri = mfaService.generateMfaSetupQrUri(user);
        return ResponseEntity.ok(qrUri);
    }

    // New DTO for MFA verification
    public record MfaVerificationDto(String username, int code) {}

    @PostMapping("/mfa/enable")
    public ResponseEntity<String> enableMfa(@RequestBody MfaVerificationDto verificationDto) {
        if (mfaService.verifyTotpCode(verificationDto.username(), verificationDto.code())) {
            User user = userService.findByUsername(verificationDto.username())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setMfaEnabled(true);
            // This is a new method you'll need to create in UserService to save the user
            userRepository.save(user);
            return ResponseEntity.ok("MFA enabled successfully!");
        }
        return ResponseEntity.badRequest().body("Invalid TOTP code.");
    }
}
