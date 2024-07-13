package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FullWeatherService {
    private final LocationRepository locationRepository;

    public Location getLocation(Location locationFromIp) {
        String cityName = locationFromIp.getCityName();
        String countryCode = locationFromIp.getCountryCode();

       return locationRepository.findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

    }

    public Location getLocationByCode(String locationCode) {
        return locationRepository.findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

    }
}
