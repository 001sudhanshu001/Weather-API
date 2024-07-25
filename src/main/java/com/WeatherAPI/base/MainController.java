package com.WeatherAPI.base;

import com.WeatherAPI.controller.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MainController {
    @GetMapping("/")
    public ResponseEntity<RootEntity> handleBaseURI() {
        return ResponseEntity.ok(createRootEntity());
    }

    private RootEntity createRootEntity() {
        RootEntity entity = new RootEntity();

        String locationsUrl =
                linkTo(methodOn(LocationApiController.class).getAll()).toString();
        entity.setLocationsUrl(locationsUrl);

        String locationByCodeUrl = linkTo(methodOn(LocationApiController.class).getByCode(null)).toString();
        entity.setLocationByCodeUrl(locationByCodeUrl);

        String realtimeWeatherByIpUrl = linkTo(
                methodOn(RealTimeWeatherController.class).getRealTimeWeatherByIPAddress(null)).toString();
        entity.setRealtimeWeatherByIpUrl(realtimeWeatherByIpUrl);

        String realtimeWeatherByCodeUrl = linkTo(
                methodOn(RealTimeWeatherController.class).getRealTimeWeatherByLocationCode(null)).toString();
        entity.setRealtimeWeatherByCodeUrl(realtimeWeatherByCodeUrl);

        String hourlyForecastByIpUrl = linkTo(
                methodOn(HourlyWeatherApiController.class).listHourlyForecastByIPAddress(null)).toString();
        entity.setHourlyForecastByIpUrl(hourlyForecastByIpUrl);

        String hourlyForecastByCodeUrl = linkTo(
                methodOn(HourlyWeatherApiController.class)
                        .listHourlyForecastByLocationCode(null, null)).toString();
        entity.setHourlyForecastByCodeUrl(hourlyForecastByCodeUrl);

        String dailyForecastByIpUrl = linkTo(
                methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).toString();
        entity.setDailyForecastByIpUrl(dailyForecastByIpUrl);

        String dailyForecastByCodeUrl = linkTo(
                methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(null)).toString();
        entity.setDailyForecastByCodeUrl(dailyForecastByCodeUrl);

        String fullWeatherByIpUrl = linkTo(
                methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).toString();
        entity.setFullWeatherByIpUrl(fullWeatherByIpUrl);

        String fullWeatherByCodeUrl = linkTo(
                methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(null)).toString();
        entity.setFullWeatherByCodeUrl(fullWeatherByCodeUrl);

        return entity;
    }

}
