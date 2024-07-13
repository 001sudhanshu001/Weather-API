package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FullWeatherService {
    private final LocationRepository locationRepository;

    public Location getLocation(Location locationFromIp) {
        String cityName = locationFromIp.getCityName();
        String countryCode = locationFromIp.getCountryCode();

       return locationRepository.findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

    }

    public Location getLocationByCode(String locationCode) {
        return locationRepository.findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

    }

    public Location update(String locationCode, Location locationInRequest) {
        Location locationInDB = locationRepository.findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

        RealTimeWeather realTimeWeather = locationInRequest.getRealTimeWeather();
        realTimeWeather.setLocation(locationInDB);

        List<DailyWeather> dailyWeather = locationInDB.getDailyWeather();
        dailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));

        List<HourlyWeather> hourlyWeatherList = locationInDB.getHourlyWeatherList();
        hourlyWeatherList.forEach(dw -> dw.getId().setLocation(locationInDB));

        // These are the fields which will not be changed, but since these fields are not in locationInRequest
        // Object so, we have to explicitly set them
        locationInRequest.setCode(locationInDB.getCode());
        locationInRequest.setCityName(locationInDB.getCityName());
        locationInRequest.setRegionName(locationInDB.getRegionName());
        locationInRequest.setCountryCode(locationInDB.getCountryCode());
        locationInRequest.setCountryName(locationInDB.getCountryName());
        locationInRequest.setEnabled(locationInDB.isEnabled());
        locationInRequest.setTrashed(locationInDB.isTrashed());

        return locationRepository.save(locationInRequest);
    }
}
