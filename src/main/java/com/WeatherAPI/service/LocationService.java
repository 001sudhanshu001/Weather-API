package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    public Location addLocation(Location location) throws CodeConflictException {
        // Checking uniqueness of code
        String code = location.getCode();
        Location byCode = locationRepository
                .findByCode(code).orElse(null);

        if(byCode != null){
            throw new CodeConflictException("This code is already used for other city");
        }
        return locationRepository.save(location);
    }

    @Cacheable(value = "locations", key = "'list'")
    public List<LocationDto> list(){
        List<Location> locations = locationRepository.findUntrashed();

        return locations.stream()
                .map(location -> modelMapper.map(location, LocationDto.class)).toList();
    }

    @Cacheable(value = "locationDTO", key = "#code")
    public LocationDto get(String code){
        Location location = locationRepository
                .findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        return modelMapper.map(location, LocationDto.class);
    }

    public Location update(Location locationInRequest) throws LocationNotFoundException {
        String code = locationInRequest.getCode();

        Location locationInDB = locationRepository
                .findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setEnabled(locationInRequest.isEnabled());

        return locationRepository.save(locationInDB);

    }

    public void delete(String code) throws LocationNotFoundException {
        if(!locationRepository.existsById(code)){
            throw new LocationNotFoundException("No Location found with the given code :" + code);
        }

        locationRepository.trashedByCode(code);
    }
}
