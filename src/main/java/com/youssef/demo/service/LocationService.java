package com.youssef.demo.service;

import com.youssef.demo.DTO.LocationPayload;
import com.youssef.demo.entity.Location;
import com.youssef.demo.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationBroadcaster locationBroadcaster;

    @Transactional
    public void saveLocations(List<LocationPayload> payloads) {
        List<Location> locations = payloads.stream()
                .map(this::toEntity)
                .toList();
        locationRepository.saveAll(locations);
        locationBroadcaster.broadcastAll(locations);
        log.debug("Saved and broadcasted {} locations", locations.size());
    }

    public List<Location> getLocationsLast24Hours() {
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        List<Location> locations = locationRepository.findByTimestampGreaterThanOrderByTimestampAsc(twentyFourHoursAgo);
        log.debug("Retrieved {} locations from last 24 hours", locations.size());
        return locations;
    }

    private Location toEntity(LocationPayload payload) {
        return Location.builder()
                .latitude(payload.getLatitude())
                .longitude(payload.getLongitude())
                .timestamp(payload.getTimestamp())
                .deviceId(payload.getDeviceId())
                .build();
    }
}
