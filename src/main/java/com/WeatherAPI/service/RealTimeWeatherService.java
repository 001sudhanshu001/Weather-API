package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dao.RealTimeWeatherRepo;
import com.WeatherAPI.dto.RealTimeWeatherDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RealTimeWeatherService {

    private final RealTimeWeatherRepo realTimeWeatherRepo;
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "realTimeWeatherDTO", key = "#location.getCountryCode() + '-' + #location.getCityName()")
    public RealTimeWeatherDto getWeatherByLocation(Location location) throws LocationNotFoundException {
        System.out.println(1);
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealTimeWeather realTimeWeather = realTimeWeatherRepo
                .findByCountryCodeAndCity(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

        return modelMapper.map(realTimeWeather, RealTimeWeatherDto.class);
    }

    @Cacheable(value = "realTimeWeatherDTO", key = "#locationCode")
    public RealTimeWeatherDto getByLocationCode(String locationCode) throws LocationNotFoundException {
        System.out.println(2);
        RealTimeWeather realTimeWeather = realTimeWeatherRepo
                .findByLocationCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(
                        "No Location found with the given code:" + locationCode
                ));

        return modelMapper.map(realTimeWeather, RealTimeWeatherDto.class);
    }

    public RealTimeWeather update(String locationCode, RealTimeWeather weather) {
        Location location = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        // Because in RealTimeWeather we are using JsonIgnore for lastUpdated and location
        weather.setLocation(location);
        weather.setLastUpdated(new Date());

        // This is for those location which does not have realtime Weather data into database, they are just in Location Table
        if (location.getRealTimeWeather() == null) {
            location.setRealTimeWeather(weather);
            Location updatedLocation = locationRepository.save(location);
            // Here no need to save the weather explicitly because of CASCADE.ALL
            return updatedLocation.getRealTimeWeather();
        }
        return realTimeWeatherRepo.save(weather);
    }
}
