package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FullWeatherService {
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "fullWeatherByLocation", key = "#location.cityName + '-' + #location.regionName +" +
            " '-' + #location.countryName + '-' + #location.countryCode")
    public FullWeatherDTO getLocation(Location location) {
        String cityName = location.getCityName();
        String countryCode = location.getCountryCode();

        Location locationInDB = locationRepository.findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

        return entity2DTO(locationInDB);
    }

    @Cacheable(value = "fullWeatherByLocationCode", key = "#locationCode")
    public FullWeatherDTO getLocationByCode(String locationCode) {
        Location location = locationRepository.findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

        return entity2DTO(location);
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

    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);

        // do not show the field location in realtime_weather object otherwise Location will be duplicate in Response
        dto.getRealTimeWeather().setLocation(null);
        return dto;
    }
}
