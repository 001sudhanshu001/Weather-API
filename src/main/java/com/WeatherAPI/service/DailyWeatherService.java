package com.WeatherAPI.service;

import com.WeatherAPI.dao.DailyWeatherRepository;
import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.DailyWeatherListDTO;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.HourlyWeatherListDto;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyWeatherService {

    private final DailyWeatherRepository dailyWeatherRepository;
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "dailyWeatherByLocation", key = "#location.cityName + '-' + #location.regionName +" +
            " '-' + #location.countryName + '-' + #location.countryCode")
    public DailyWeatherListDTO getByLocation(Location location) {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository
                .findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(countryCode, cityName));

        List<DailyWeather> dailyWeatherList = dailyWeatherRepository.findByLocationCode(locationInDB.getCode());
        return listEntity2DTO(dailyWeatherList);
    }

    @Cacheable(value = "dailyWeatherByCode", key = "#locationCode")
    public DailyWeatherListDTO getByLocationCode(String locationCode) {
        Location location = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

        List<DailyWeather> dailyWeatherList = dailyWeatherRepository.findByLocationCode(locationCode);
        return listEntity2DTO(dailyWeatherList);
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

    private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
        Location location = dailyForecast.get(0).getId().getLocation();

        DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
        listDTO.setLocation(location.toString());

        dailyForecast.forEach(dailyWeather -> {
            listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
        });

        return listDTO;
    }


}
