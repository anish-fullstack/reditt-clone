package com.epsilon.redit.security;

import com.epsilon.redit.exception.SpringRedittException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

@Service
public class JwtProvider {

    public static final long EXPIRATION_TIME = 3_600_000; // 60 mins

    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("redit.jks.old");
            keyStore.load(resourceAsStream, "password".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedittException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey(){
        try {
            return (PrivateKey) keyStore.getKey("reditt", "password".toCharArray());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new SpringRedittException("Exception occurred while retrieving public key from keystore");
        }
    }
}
