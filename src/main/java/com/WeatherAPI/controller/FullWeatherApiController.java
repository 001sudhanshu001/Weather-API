package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.BadRequestException;
import com.WeatherAPI.service.FullWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/full")
@RequiredArgsConstructor
public class FullWeatherApiController {

    private final GeoLocationService geoLocationService;
    private final FullWeatherService fullWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RateLimited
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        Location locationFromIP = geoLocationService.getLocationFromIpAddress(ipAddress);
        Location locationInDB = fullWeatherService.getLocation(locationFromIP);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @GetMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode) {

        Location locationInDB = fullWeatherService.getLocationByCode(locationCode);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @PutMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> updateFullWeather(@PathVariable String locationCode,
                                               @RequestBody @Valid FullWeatherDTO dto)  {

        if (dto.getHourlyWeatherList().isEmpty()) {
            throw new BadRequestException("Hourly weather data cannot be empty");
        }

        if (dto.getDailyWeather().isEmpty()) {
            throw new BadRequestException("Daily weather data cannot be empty");
        }

        Location locationInRequest = dto2Entity(dto);

        Location updatedLocation = fullWeatherService.update(locationCode, locationInRequest);

        return ResponseEntity.ok(entity2DTO(updatedLocation));
    }

    
    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);

        // do not show the field location in realtime_weather object otherwise Location will be duplicate in Response
        dto.getRealTimeWeather().setLocation(null);
        return dto;
    }

    private Location dto2Entity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }

}
