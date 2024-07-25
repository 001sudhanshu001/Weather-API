package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.RealTimeWeatherDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.RealTimeWeatherService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/realtime")
@RequiredArgsConstructor
public class RealTimeWeatherController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeWeatherController.class);
    private final GeoLocationService geoLocationService;
    private final RealTimeWeatherService realTimeWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RateLimited
    public ResponseEntity<?> getRealTimeWeatherByIPAddress(HttpServletRequest request){
        String ipAddress = CommonUtility.getIpAddress(request);

        try {
            Location locationFromIpAddress = geoLocationService.getLocationFromIpAddress(ipAddress);
            System.out.println("Location From IP Address" + locationFromIpAddress);

            // Weather we have real Time data about this particular location otherwise throw LocationNotFoundException
            RealTimeWeather realTimeWeather = realTimeWeatherService.getWeatherByLocation(locationFromIpAddress);

            RealTimeWeatherDto weatherDto = modelMapper.map(realTimeWeather, RealTimeWeatherDto.class);
            return ResponseEntity.ok(weatherDto);

        } catch (GeoLocationException e) {
            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> getRealTimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode){
        try{
            RealTimeWeather byLocationCode = realTimeWeatherService.getByLocationCode(locationCode);
            RealTimeWeatherDto weatherDto = modelMapper.map(byLocationCode, RealTimeWeatherDto.class);

            return ResponseEntity.ok(weatherDto);
        }catch (LocationNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> updateRealTimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode,
                                                                 @RequestBody @Valid RealTimeWeatherDto dtoInRequest) {
        try {
            RealTimeWeather realTimeWeather = modelMapper.map(dtoInRequest, RealTimeWeather.class);
            realTimeWeather.setLocationCode(locationCode);

            RealTimeWeather updatedRealTimeWeather = realTimeWeatherService.update(locationCode, realTimeWeather);

            RealTimeWeatherDto weatherDto = modelMapper.map(updatedRealTimeWeather, RealTimeWeatherDto.class);

            return ResponseEntity.ok(weatherDto);
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
