package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.BadRequestException;
import com.WeatherAPI.service.FullWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

        FullWeatherDTO fullWeatherDTO = entity2DTO(locationInDB);
        return ResponseEntity.ok(addLinksByIp(fullWeatherDTO));
    }

    @GetMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode) {

        Location locationInDB = fullWeatherService.getLocationByCode(locationCode);

        FullWeatherDTO fullWeatherDTO = entity2DTO(locationInDB);
        return ResponseEntity.ok(addLinksByLocation(entity2DTO(locationInDB), locationCode));
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
        FullWeatherDTO fullWeatherDTO = entity2DTO(updatedLocation);
        return ResponseEntity.ok(addLinksByLocation(fullWeatherDTO, locationCode));
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

    private EntityModel<FullWeatherDTO> addLinksByLocation(FullWeatherDTO dto, String locationCode) {
        return EntityModel.of(dto)
                .add(linkTo(
                        methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode))
                        .withSelfRel());
    }

    private EntityModel<FullWeatherDTO> addLinksByIp(FullWeatherDTO dto) {
        return EntityModel.of(dto)
                .add(linkTo(
                        methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null))
                        .withSelfRel());
    }

}
