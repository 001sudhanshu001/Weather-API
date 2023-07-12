package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepo;
import com.WeatherAPI.dao.RealTimeWeatherRepo;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RealTimeWeatherService {

    private final RealTimeWeatherRepo realTimeWeatherRepo;
    private final LocationRepo locationRepo;

    public RealTimeWeather getLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealTimeWeather realTimeWeather = realTimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);

        if(realTimeWeather == null){
            throw new LocationNotFoundException("No Location found with the given country code and city name");
        }
        return realTimeWeather;
    }

    public RealTimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
        RealTimeWeather byLocationCode = realTimeWeatherRepo.findByLocationCode(locationCode);

        if(byLocationCode == null){
            throw new LocationNotFoundException("No Location found with the given code:" + locationCode);
        }
        return byLocationCode;
    }

    public RealTimeWeather update(String locationCode, RealTimeWeather weather) throws LocationNotFoundException {
        Location location = locationRepo.findByCode(locationCode);
        if(location == null){
            throw new LocationNotFoundException("No Location found with the given code:" + locationCode);
        }
        // Because in RealTimeWeather we are using JsonIgnore for lastUpdated and location
        weather.setLocation(location);
        weather.setLastUpdated(new Date());
        // This is for those location which does not have realtime Weather data into database, they are just in Location Table
        // Realtime_weather table mai tabhi toh ja skte mai jab kisi Location ke saath associated ho
        if (location.getRealTimeWeather() == null) { //
            location.setRealTimeWeather(weather);
            Location updatedLocation = locationRepo.save(location);
            // Here no need to save the weather explicity because of CASCADE.ALL
            return updatedLocation.getRealTimeWeather();
        } //
        return realTimeWeatherRepo.save(weather);
    }
}
