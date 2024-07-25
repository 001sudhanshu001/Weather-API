package com.WeatherAPI.controller;


import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.HourlyWeatherListDto;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.BadRequestException;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import com.WeatherAPI.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@RequiredArgsConstructor
@Validated // So that HourlyWeatherDto provided in the list as JSON can be validated
public class HourlyWeatherApiController {
    private  final HourlyWeatherService hourlyWeatherService;
    private final GeoLocationService geoLocationService;

    private final ModelMapper modelMapper;

    @GetMapping         //request is required to get ip and hour of the day
    @RateLimited
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIpAddress(request);

        try {
            // Fetching hour of the day from the header
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));// This can throw NumberFormatException if header is null
            Location locationFromIp = geoLocationService.getLocationFromIpAddress(ipAddress);

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIp, currentHour);

            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));

        } catch (GeoLocationException | NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    //request is required to get hour of the day. This method will return the Weather of upcoming hours
    // i.e. if current hour is 5 then it will then it will return the Weather after 4 hours
    @GetMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode,
                                                              HttpServletRequest request) {
        try{
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);

            if(hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));

        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{locationCode}")
    @RateLimited
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode,
                        @RequestBody @Valid List<HourlyWeatherDto> listDto) throws BadRequestException {

        if(listDto.isEmpty()){
           throw new BadRequestException("Hourly Forecast Data can't be empty");
        }

        List<HourlyWeather> hourlyWeathersEntity = listDto2Entity(listDto);

        try {
            List<HourlyWeather> updatedHourlyWeather =
                    hourlyWeatherService.updateByLocationCode(locationCode, hourlyWeathersEntity);

            return ResponseEntity.ok(listEntity2DTO(updatedHourlyWeather));

        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    private HourlyWeatherListDto listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location = hourlyForecast.get(0).getId().getLocation();

        HourlyWeatherListDto listDto = new HourlyWeatherListDto();
        listDto.setLocation(location.toString());

        hourlyForecast.forEach((hourlyWeather -> {
            HourlyWeatherDto dto = modelMapper.map(hourlyWeather, HourlyWeatherDto.class);
            listDto.addWeatherHourlyDto(dto);
        }));

        return listDto;
    }


    private List<HourlyWeather> listDto2Entity(List<HourlyWeatherDto> listDto) {
        List<HourlyWeather> listEntity = new ArrayList<>();

        listDto.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, HourlyWeather.class));
        });

        return listEntity;
    }

}
