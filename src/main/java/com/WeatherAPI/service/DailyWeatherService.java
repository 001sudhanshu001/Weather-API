package com.WeatherAPI.service;

import com.WeatherAPI.dao.DailyWeatherRepository;
import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<DailyWeather> getByLocationCode(String locationCode) {
        Location location = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

        return dailyWeatherRepository.findByLocationCode(locationCode);
    }

    public List<DailyWeather> updateByLocationCode(String code, List<DailyWeather> dailyWeatherInRequest)
            throws LocationNotFoundException {
        Location location = locationRepository
                .findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException(code));

        for (DailyWeather data : dailyWeatherInRequest) {
            data.getId().setLocation(location);
        }

        List<DailyWeather> dailyWeatherInDB = location.getDailyWeather();
        List<DailyWeather> dailyWeatherToBeRemoved = new ArrayList<>();

        for (DailyWeather forecast : dailyWeatherInDB) {
            if (!dailyWeatherInRequest.contains(forecast)) {
                dailyWeatherToBeRemoved.add(forecast.getShallowCopy());
            }
        }

        for (DailyWeather forecastToBeRemoved : dailyWeatherToBeRemoved) {
            dailyWeatherInDB.remove(forecastToBeRemoved);
        }

        return dailyWeatherRepository.saveAll(dailyWeatherInRequest);
    }
}
