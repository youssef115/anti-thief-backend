package com.youssef.demo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youssef.demo.DTO.EncryptedPayload;
import com.youssef.demo.DTO.LocationPayload;
import com.youssef.demo.config.SecurityConfig;
import com.youssef.demo.entity.Location;
import com.youssef.demo.service.LocationService;
import com.youssef.demo.util.PolylineEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final SecurityConfig securityConfig;

    @PostMapping("/location")
    public ResponseEntity<String> receiveLocations(@Valid @RequestBody List<LocationPayload> payloads) {
        log.info("Received unencrypted location batch, count: {}", payloads.size());
        locationService.saveLocations(payloads);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/secure/location")
    public ResponseEntity<String> receiveEncryptedLocations(@Valid @RequestBody EncryptedPayload payload) {
        log.info("Received encrypted location batch");

        try {
            String decryptedJson = decrypt(payload.getData());

            if (decryptedJson == null) {
                log.warn("Decryption failed for incoming payload");
                return ResponseEntity.status(400).body("Decryption failed");
            }

            log.debug("Decryption successful");

            Gson gson = new Gson();
            List<LocationPayload> locations = gson.fromJson(decryptedJson,
                    new TypeToken<List<LocationPayload>>(){}.getType());

            locationService.saveLocations(locations);
            log.info("Processed encrypted location batch, count: {}", locations.size());

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Error processing encrypted payload", e);
            return ResponseEntity.status(500).body("Error processing request");
        }
    }

    @GetMapping("/locations/24h")
    public ResponseEntity<String> getPositionOfLast24Hours() {
        log.debug("Fetching locations for last 24 hours");
        List<Location> locations = locationService.getLocationsLast24Hours();
        String encodedPolyline = PolylineEncoder.encode(locations);
        return ResponseEntity.ok(encodedPolyline);
    }

    private String decrypt(String encryptedBase64) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(securityConfig.getAesKey().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[combined.length - 12];
            System.arraycopy(combined, 0, iv, 0, 12);
            System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Decryption error", e);
            return null;
        }
    }
}
