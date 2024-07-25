package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.DailyWeatherListDTO;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.BadRequestException;
import com.WeatherAPI.service.DailyWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/daily")
@Validated
@RequiredArgsConstructor
public class DailyWeatherApiController {

    private final DailyWeatherService dailyWeatherService;
    private final GeoLocationService locationService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RateLimited
    public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        Location locationFromIP = locationService.getLocationFromIpAddress(ipAddress);
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        DailyWeatherListDTO dto = listEntity2DTO(dailyForecast);
        return ResponseEntity.ok(addLinksByIP(dto));
    }

    @GetMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode) {
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        DailyWeatherListDTO dto = listEntity2DTO(dailyForecast);

        return ResponseEntity.ok(addLinksByLocation(dto, locationCode));
    }

    @PutMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code,
                                                 @RequestBody @Valid List<DailyWeatherDTO> listDTO)
            throws BadRequestException {

        if (listDTO.isEmpty()) {
            throw new BadRequestException("Daily forecast data cannot be empty");
        }

        listDTO.forEach(System.out::println);
        List<DailyWeather> dailyWeather = listDTO2ListEntity(listDTO);

        List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeather);

        DailyWeatherListDTO updatedDto = listEntity2DTO(updatedForecast);

        return ResponseEntity.ok(addLinksByLocation(updatedDto, code));
    }


    private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
        Location location = dailyForecast.get(0).getId().getLocation();

        DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
        listDTO.setLocation(location.toString());

        dailyForecast.forEach(dailyWeather -> {
            listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
        });

        return listDTO;
    }

    private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> listDTO) {
        List<DailyWeather> listEntity = new ArrayList<>();

        listDTO.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, DailyWeather.class));
        });

        return listEntity;
    }

    private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dto) {

        return EntityModel.of(dto)
                .add(linkTo(
                     methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null))
                    .withSelfRel())

                .add(linkTo(
                     methodOn(RealTimeWeatherController.class).getRealTimeWeatherByIPAddress(null))
                    .withRel("realtime_weather"))

                .add(linkTo(
                     methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null))
                    .withRel("hourly_forecast"))

                .add(linkTo(
                     methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null))
                    .withRel("full_forecast"));

    }

    private EntityModel<DailyWeatherListDTO> addLinksByLocation(DailyWeatherListDTO dto, String locationCode) {

        return EntityModel.of(dto)
                .add(linkTo(
                        methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode))
                        .withSelfRel())

                .add(linkTo(
                        methodOn(RealTimeWeatherController.class).getRealTimeWeatherByLocationCode(locationCode))
                        .withRel("realtime_weather"))

                .add(linkTo(
                        methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null))
                        .withRel("hourly_forecast"))

                .add(linkTo(
                        methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode))
                        .withRel("full_forecast"));
    }
}
