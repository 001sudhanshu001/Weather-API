package com.WeatherAPI.service;

import com.WeatherAPI.dao.HourlyWeatherRepository;
import com.WeatherAPI.dao.LocationRepo;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherInRequest)
                                    throws LocationNotFoundException {

        Location location = locationRepo.findByCode(locationCode);

        if(location == null){
            throw new LocationNotFoundException("No Location Found with the given code");
        }

        // Setting locationCode for each HourlyWeather
        for (HourlyWeather item : hourlyWeatherInRequest) {
            item.getId().setLocation(location);
        }

        // While Updating we will remove old
        List<HourlyWeather> hourlyWeatherInDB = location.getHourlyWeatherList();
        List<HourlyWeather> hourlyWeatherToBeRemvoved = new ArrayList<>();

        for(HourlyWeather item : hourlyWeatherInDB) {
            if(!hourlyWeatherInRequest.contains(item)) {
                hourlyWeatherToBeRemvoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : hourlyWeatherToBeRemvoved) {
            hourlyWeatherInDB.remove(item);
        }

        return hourlyRepo.saveAll(hourlyWeatherInRequest);
    }

}
