package com.WeatherAPI.controller;


import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@RequiredArgsConstructor
public class HourlyWeatherApiController {
    private  final HourlyWeatherService hourlyWeatherService;
    private final GeoLocationService geoLocationService;

    @GetMapping
    public ResponseEntity<?> listHourlyForcastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);
        
        int currentHour = Integer.parseInt("X-Current-Hour");

        try {
            Location locationFromIp = geoLocationService.getLocationFromIpAddress(ipAddress);

            List<HourlyWeather> hourlyForcast = hourlyWeatherService.getByLocationCode(locationFromIp, currentHour);

            if(hourlyForcast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(hourlyForcast);

        } catch (GeoLocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

}
