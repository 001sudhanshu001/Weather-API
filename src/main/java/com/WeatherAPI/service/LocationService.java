package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepo;
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
    private final LocationRepo locationRepo;

    public Location addLocation(Location location) throws CodeConflictException {
        // Checking uniqueness of code
        String code = location.getCode();
        Location byCode = locationRepo.findByCode(code);
        if(byCode != null){
            throw new CodeConflictException("This code is already used for other city");
        }
        return locationRepo.save(location);
    }

    public List<Location> list(){
        return locationRepo.findUntrashed();
    }

    public Location get(String code){
        return locationRepo.findByCode(code);
    }

    public Location update(Location locationInRequest) throws LocationNotFoundException {
        String code = locationInRequest.getCode();

        Location locationInDB = locationRepo.findByCode(code);

        if(locationInDB == null){
            throw new LocationNotFoundException("No Location found with the given code : " + code);
        }

        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setEnabled(locationInRequest.isEnabled());

        return locationRepo.save(locationInDB);

    }

    public void delete(String code) throws LocationNotFoundException {
        if(!locationRepo.existsById(code)){
            throw new LocationNotFoundException("No Location found with the given code :" + code);
        }
        locationRepo.trashedByCode(code);
    }
}
