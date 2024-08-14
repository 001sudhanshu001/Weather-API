package com.WeatherAPI.service;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Deprecated
    @Cacheable(value = "locations", key = "'list'")
    public List<LocationDto> list(){
        List<Location> locations = locationRepository.findUntrashed();

        return locations.stream()
                .map(location -> modelMapper.map(location, LocationDto.class)).toList();
    }


    @Cacheable(value = "locationsPage", key = "#pageNum + '-' + #pageSize + '-' + #sortOption")
    public Page<LocationDto> listByPage(int pageNum, int pageSize, String sortOption) {
        Sort sort = Sort.by(sortOption).ascending();

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Location> locationPage = locationRepository.findUntrashed(pageable);
        if(locationPage.isEmpty()) {
            return null;
        }

        return convertToDTOPage(locationPage);
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

    private List<LocationDto> listEntity2ListDTO(List<Location> listEntity) {

        return listEntity.stream().map(this::entity2DTO)
                .collect(Collectors.toList());

    }

    private LocationDto entity2DTO(Location entity) {
        return modelMapper.map(entity, LocationDto.class);
    }

    public Page<LocationDto> convertToDTOPage(Page<Location> locationPage) {
        List<LocationDto> locationDTOs = locationPage.stream()
                .map(location -> modelMapper.map(location, LocationDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(locationDTOs, locationPage.getPageable(), locationPage.getTotalElements());
    }

}
