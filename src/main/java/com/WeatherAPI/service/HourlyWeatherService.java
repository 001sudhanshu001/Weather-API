package com.WeatherAPI.service;

import com.WeatherAPI.dao.HourlyWeatherRepository;
import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.HourlyWeatherListDto;
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
public class HourlyWeatherService {
    private final HourlyWeatherRepository hourlyRepo;
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;


    @Cacheable(value = "hourlyWeatherByLocation", key = "#location.cityName + '-' + #location.regionName +" +
            " '-' + #location.countryName + '-' + #location.countryCode + '-' + #currentHour")
    public HourlyWeatherListDto getByLocation(Location location, int currentHour)
            throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository
                .findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException(
                        "No Location Found with the given country code and city name"
                ));

        List<HourlyWeather> hourlyWeatherList = hourlyRepo.findByLocationCode(locationInDB.getCode(), currentHour);
        return listEntity2DTO(hourlyWeatherList);

    }

    @Cacheable(value = "hourlyWeatherByLocationCode", key = "#locationCode + #currentHour")
    public HourlyWeatherListDto getByLocationCode(String locationCode, int currentHour)
            throws LocationNotFoundException {
        Location locationInDB = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        List<HourlyWeather> hourlyWeatherList = hourlyRepo.findByLocationCode(locationCode, currentHour);
        return listEntity2DTO(hourlyWeatherList);

    }

    public List<HourlyWeather> updateByLocationCode(String locationCode,
                                                    List<HourlyWeather> hourlyWeatherInRequest)
                                    throws LocationNotFoundException {

        Location location = locationRepository
                .findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        // Setting locationCode for each HourlyWeather
        for (HourlyWeather item : hourlyWeatherInRequest) {
            item.getId().setLocation(location);
        }

        // While Updating we will remove old
        List<HourlyWeather> hourlyWeatherInDB = location.getHourlyWeatherList();
        List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

        for(HourlyWeather item : hourlyWeatherInDB) {
            if(!hourlyWeatherInRequest.contains(item)) {
                hourlyWeatherToBeRemoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : hourlyWeatherToBeRemoved) {
            hourlyWeatherInDB.remove(item);
        }

        return hourlyRepo.saveAll(hourlyWeatherInRequest);
    }

    private HourlyWeatherListDto listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location = hourlyForecast.get(0).getId().getLocation();

        HourlyWeatherListDto listDto = new HourlyWeatherListDto();
        listDto.setLocation(location.toString());

        hourlyForecast.forEach((hourlyWeather -> {
            HourlyWeatherDto dto = modelMapper.map(hourlyWeather, HourlyWeatherDto.class);
            listDto.addWeatherHourlyDto(dto);
        }));

        return listDto;
    }

}
