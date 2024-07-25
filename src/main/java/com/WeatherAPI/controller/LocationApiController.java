package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationApiController {
    private final LocationService service;
    private final ModelMapper modelMapper;

    @PostMapping
    @RateLimited
    public ResponseEntity<?> addLocation(@RequestBody @Valid LocationDto locationDto){
        LocationDto addedLocation;
        try {
            Location location = modelMapper.map(locationDto, Location.class);
            addedLocation = modelMapper.map(service.addLocation(location), LocationDto.class);
        } catch (CodeConflictException e) {
            return new ResponseEntity<>("This code is already used for other city",HttpStatus.CONFLICT);
        }
        URI uri = URI.create("/v1/location/" + addedLocation.getCode());
        return ResponseEntity.created(uri).body(addedLocation);
    }

    @GetMapping
    @RateLimited
    public ResponseEntity<?> getAll(){
        List<LocationDto> dtoList = service.list();
        if(dtoList.isEmpty()){
            return ResponseEntity.noContent().build(); // status code 204
        }
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    @RateLimited
    public ResponseEntity<?> getByCode(@PathVariable("code") String code){
        LocationDto locationDto = service.get(code);

        return ResponseEntity.ok(locationDto);
    }

    @PutMapping
    @RateLimited
    public ResponseEntity<?> updateByCode(@RequestBody @Valid LocationDto locationDto){
        Location location = modelMapper.map(locationDto, Location.class);
        Location updatedLocation = service.update(location);

        return ResponseEntity.ok(modelMapper.map(updatedLocation, LocationDto.class));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
        service.delete(code);
        return ResponseEntity.noContent().build();
    }

}
