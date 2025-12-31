package com.youssef.demo.controller;

import com.youssef.demo.entity.Location;
import com.youssef.demo.service.LocationBroadcaster;
import com.youssef.demo.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final LocationBroadcaster locationBroadcaster;
    private final LocationService locationService;

    @GetMapping("/map")
    public String showMap() {
        return "map";
    }

    @GetMapping("/map/{deviceId}")
    public String showMapForDevice(@PathVariable String deviceId, Model model) {
        model.addAttribute("deviceId", deviceId);
        return "map";
    }

    @GetMapping(path = "/api/locations/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter streamLocations() {
        return locationBroadcaster.subscribe();
    }

    @GetMapping("/api/locations/latest")
    @ResponseBody
    public List<Location> getLatestLocations() {
        return locationService.getLocationsLast24Hours();
    }

  
}
