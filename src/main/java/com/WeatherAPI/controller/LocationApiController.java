package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationApiController {
    private final LocationService service;
    private final ModelMapper modelMapper;

    @PostMapping
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
        List<Location> locations = service.list();

        if(locations.isEmpty()){
            return ResponseEntity.noContent().build(); // status code 204
        }

        List<LocationDto> dtoList = locations.stream()
                .map(location -> modelMapper.map(location, LocationDto.class)).toList();

        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    @RateLimited
    public ResponseEntity<?> getByCode(@PathVariable("code") String code){
        Location location = service.get(code);

        return ResponseEntity.ok(modelMapper.map(location, LocationDto.class));
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
