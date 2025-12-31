package com.youssef.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EncryptedPayload {

    @NotBlank(message = "Encrypted data is required")
    private String data;

    @NotBlank(message = "Device ID is required")
    @Size(max = 64, message = "Device ID must be <= 64 characters")
    private String deviceId;

    @NotNull(message = "Timestamp is required")
    private Long timestamp;
}
