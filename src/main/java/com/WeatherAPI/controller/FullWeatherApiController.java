package com.WeatherAPI.controller;

import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.service.FullWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/full")
@RequiredArgsConstructor
public class FullWeatherApiController {

    private final GeoLocationService geoLocationService;
    private final FullWeatherService fullWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        Location locationFromIP = geoLocationService.getLocationFromIpAddress(ipAddress);
        Location locationInDB = fullWeatherService.getLocation(locationFromIP);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);

        // do not show the field location in realtime_weather object other wise Location will be duplicate in Response
        dto.getRealTimeWeather().setLocation(null);
        return dto;
    }

    private Location dto2Entity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }

}
