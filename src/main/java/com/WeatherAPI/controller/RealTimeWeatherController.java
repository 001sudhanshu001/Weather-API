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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

            // Weather we have real Time data about this particular location otherwise throw LocationNotFoundException
            RealTimeWeatherDto weatherDto = realTimeWeatherService.getWeatherByLocation(locationFromIpAddress);

            return ResponseEntity.ok(addLinksByIP(weatherDto));

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
            RealTimeWeatherDto weatherDto = realTimeWeatherService.getByLocationCode(locationCode);

            return ResponseEntity.ok(addLinksByLocation(weatherDto, locationCode));

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

            RealTimeWeatherDto updatedDto = modelMapper.map(updatedRealTimeWeather, RealTimeWeatherDto.class);

            return ResponseEntity.ok(addLinksByLocation(updatedDto, locationCode));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private RealTimeWeatherDto addLinksByIP(RealTimeWeatherDto dto) {

        dto.add(linkTo(
                methodOn(RealTimeWeatherController.class).getRealTimeWeatherByIPAddress(null))
                .withSelfRel());

        dto.add(linkTo(
                methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null))
                .withRel("hourly_forecast"));

        dto.add(linkTo(
                methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null))
                .withRel("daily_forecast"));

        dto.add(linkTo(
                methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null))
                .withRel("full_forecast"));

        return dto;
    }

    private RealTimeWeatherDto addLinksByLocation(RealTimeWeatherDto dto, String locationCode) {

        dto.add(linkTo(
                methodOn(RealTimeWeatherController.class).getRealTimeWeatherByLocationCode(locationCode))
                .withSelfRel());

        dto.add(linkTo(
                methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null))
                .withRel("hourly_forecast"));

        dto.add(linkTo(
                methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode))
                .withRel("daily_forecast"));

        dto.add(linkTo(
                methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode))
                .withRel("full_forecast"));

        return dto;
    }

}
