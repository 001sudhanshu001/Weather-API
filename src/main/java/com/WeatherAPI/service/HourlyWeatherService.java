package com.WeatherAPI.service;

import com.WeatherAPI.dao.HourlyWeatherRepository;
import com.WeatherAPI.dao.LocationRepo;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {
    private final HourlyWeatherRepository hourlyRepo;
    private final LocationRepo locationRepo;

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepo. findByCountryNameAndCityName(countryCode, cityName);

        if(locationInDB == null){
            throw new LocationNotFoundException("No Location Found with the given country code and city name");
        }

        return hourlyRepo.findByLocationCode(locationInDB.getCode(), currentHour);

    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) throws LocationNotFoundException {
        Location locationInDB = locationRepo.findByCode(locationCode);

        if(locationInDB == null){
            throw new LocationNotFoundException("No Location Found with the given code");
        }

        return hourlyRepo.findByLocationCode(locationCode, currentHour);

    }


}
