package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;

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

    public List<Location> list(){
        return locationRepository.findUntrashed();
    }

    public Location get(String code){
        return locationRepository
                .findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));
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
