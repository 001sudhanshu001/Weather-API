package com.WeatherAPI.service;

import com.WeatherAPI.dao.DailyWeatherRepository;
import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyWeatherService {

    private final DailyWeatherRepository dailyWeatherRepository;
    private final LocationRepository locationRepository;

    public List<DailyWeather> getByLocation(Location location) {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository
                .findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

        return dailyWeatherRepository.findByLocationCode(locationInDB.getCode());


    }
}