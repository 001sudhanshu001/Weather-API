package com.WeatherAPI.controller;

import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.DailyWeatherListDTO;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.service.DailyWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/daily")
@Validated
@RequiredArgsConstructor
public class DailyWeatherApiController {

    private final DailyWeatherService dailyWeatherService;
    private final GeoLocationService locationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        Location locationFromIP = locationService.getLocationFromIpAddress(ipAddress);
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<DailyWeatherDTO> dailyWeatherDTOS = dailyForecast.stream()
                .map(dailyWeather -> modelMapper.map(dailyForecast, DailyWeatherDTO.class)).toList();

        return ResponseEntity.ok(dailyWeatherDTOS);
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode) {
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        DailyWeatherListDTO dto = listEntity2DTO(dailyForecast);

        return ResponseEntity.ok(dto);
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
}
