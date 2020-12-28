package com.epsilon.redit.service.serviceImpl;

import com.epsilon.redit.dao.PortalUserRepository;
import com.epsilon.redit.dao.VerificationTokenRepository;
import com.epsilon.redit.dto.AuthenticationResponse;
import com.epsilon.redit.dto.LoginDto;
import com.epsilon.redit.dto.SignUpDto;
import com.epsilon.redit.exception.SpringRedittException;
import com.epsilon.redit.model.NotificationEmail;
import com.epsilon.redit.model.PortalUser;
import com.epsilon.redit.model.VerificationToken;
import com.epsilon.redit.security.JwtProvider;
import com.epsilon.redit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final PortalUserRepository portalUserRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    @Transactional
    @Override
    public void signUp(SignUpDto signUpDto) {
        PortalUser portalUser = new PortalUser();
        portalUser.setUsername(signUpDto.getUsername().trim());
        portalUser.setEmail(signUpDto.getEmail().trim());
        portalUser.setCreatedDate(Instant.now());
        portalUser.setEnabled(false);
        portalUser.setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword().trim()));
        portalUserRepository.save(portalUser);
       String token =  generateVerificationToken(portalUser);

       mailService.sendMail(new NotificationEmail("Account Verification", signUpDto.getEmail(),
               "Thank you for signing up to Spring Reddit, Please click the url below to activate your account:"+
               "http://localhost:8080/api/v1/auth/accountVerification/"+token));
    }

    @Override
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedittException("Invalid token"));
        fetchPortalUserAndEnable(verificationToken.get());
    }

    @Override
    public AuthenticationResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new AuthenticationResponse(token, loginDto.getUsername());
    }

    @Transactional
    protected void fetchPortalUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getPortalUser().getUsername();
        PortalUser portalUser = portalUserRepository.findByUsername(username).orElseThrow(() -> new SpringRedittException(String.format("Username %s does not exist", username)));
        portalUser.setEnabled(true);
        portalUserRepository.save(portalUser);
    }

    @Transactional
    protected String generateVerificationToken(PortalUser portalUser){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setPortalUser(portalUser);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
