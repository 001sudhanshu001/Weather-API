package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
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
    private final LocationRepository locationRepository;

    public RealTimeWeather getWeatherByLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        return realTimeWeatherRepo
                .findByCountryCodeAndCity(countryCode, cityName)
                .orElseThrow(() ->  new LocationNotFoundException(countryCode, cityName));
    }

    public RealTimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {

        return realTimeWeatherRepo
                .findByLocationCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location found with the given code:" + locationCode));
    }

    public RealTimeWeather update(String locationCode, RealTimeWeather weather) throws LocationNotFoundException {
        Location location = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        // Because in RealTimeWeather we are using JsonIgnore for lastUpdated and location
        weather.setLocation(location);
        weather.setLastUpdated(new Date());
        // This is for those location which does not have realtime Weather data into database, they are just in Location Table
        if (location.getRealTimeWeather() == null) { //
            location.setRealTimeWeather(weather);
            Location updatedLocation = locationRepository.save(location);
            // Here no need to save the weather explicitly because of CASCADE.ALL
            return updatedLocation.getRealTimeWeather();
        }
        return realTimeWeatherRepo.save(weather);
    }
}
