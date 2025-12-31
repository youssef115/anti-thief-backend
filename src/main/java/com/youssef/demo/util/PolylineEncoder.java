package com.youssef.demo.util;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.youssef.demo.entity.Location;

import java.util.List;

public class PolylineEncoder {

    public static String encode(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return "";
        }

        List<LatLng> points = locations.stream()
                .map(loc -> new LatLng(loc.getLatitude(), loc.getLongitude()))
                .toList();

        return PolylineEncoding.encode(points);
    }
}
