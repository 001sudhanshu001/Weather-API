package com.WeatherAPI.controller;


import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.HourlyWeatherListDto;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listHourlyForcastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        try {
            // Fetching hour of the day from the header
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));// This can throw NumberFormatException if header is null

            Location locationFromIp = geoLocationService.getLocationFromIpAddress(ipAddress);

            List<HourlyWeather> hourlyForcast = hourlyWeatherService.getByLocationCode(locationFromIp, currentHour);

            if(hourlyForcast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForcast));

        } catch (GeoLocationException | NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    private HourlyWeatherListDto listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location = hourlyForecast.get(0).getId().getLocation(); // get Location

        HourlyWeatherListDto listDto = new HourlyWeatherListDto();
        listDto.setLocation(location.toString());

        hourlyForecast.forEach((hourlyWeather -> {
            HourlyWeatherDto dto = modelMapper.map(hourlyWeather, HourlyWeatherDto.class);
            listDto.addWeatherHourlyDto(dto);
        }));

        return listDto;
    }

}
