package com.vivek.garg.book_network.auth;

import com.vivek.garg.book_network.email.EmailService;
import com.vivek.garg.book_network.email.EmailTemplateName;
import com.vivek.garg.book_network.role.RoleRepository;
import com.vivek.garg.book_network.security.JwtService;
import com.vivek.garg.book_network.user.Token;
import com.vivek.garg.book_network.user.TokenRepo;
import com.vivek.garg.book_network.user.User;
import com.vivek.garg.book_network.user.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public void register(@RequestBody @Valid RegistrationRequest registrationRequest) throws MessagingException {
        var userRole = roleRepo.findByName("USER")
                .orElseThrow(()-> new IllegalArgumentException("no role exist."));

        var user = User
                .builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepo.save(user);
        sendValidationEmail(
                user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationToken(6);

        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepo.save(token);

        return generatedToken;
    }

    private String generateActivationToken(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i =0;i<length;i++){
            int random = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(random));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var token = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder().token(token).build();
    }

//    @Transactional
    public void confirm(String token) throws MessagingException {
        Token saved = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if(LocalDateTime.now().isAfter(saved.getExpiresAt())){
            sendValidationEmail(saved.getUser());
            throw new RuntimeException("Activation Token has expirred. New Email is sent");
        }

        var user = userRepo.findById(saved.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepo.save(user);
        saved.setValidatedAt(LocalDateTime.now());
        tokenRepo.save(saved);
    }
}
