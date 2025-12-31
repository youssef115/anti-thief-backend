package com.youssef.demo.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LocationPayload {

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private double longitude;

    @Positive(message = "Timestamp must be positive")
    private long timestamp;

    @NotBlank(message = "Device ID is required")
    @Size(max = 64, message = "Device ID must be <= 64 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Device ID contains invalid characters")
    private String deviceId;
}
