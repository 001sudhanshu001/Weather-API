package com.WeatherAPI.controller;

import com.WeatherAPI.aop.RateLimited;
import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.BadRequestException;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
@Validated
public class LocationApiController {
    private final LocationService service;
    private final ModelMapper modelMapper;

    private static Map<String, String> propertyMap = Map.of(
            "code", "code",
            "city_name", "cityName",
            "region_name", "regionName",
            "country_code", "countryCode",
            "country_name", "countryName",
            "enabled", "enabled",
            "cityName", "cityName",
            "regionName", "regionName",
            "countryCode", "countryCode",
            "countryName", "countryName"
    );

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

    @Deprecated
   // @GetMapping
    @RateLimited
    public ResponseEntity<?> getAll(){
        List<LocationDto> dtoList = service.list();
        if(dtoList.isEmpty()){
            return ResponseEntity.noContent().build(); // status code 204
        }
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @RateLimited
    @GetMapping
    public ResponseEntity<?> listLocations(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(value = 1)	Integer pageNum,
            @RequestParam(value = "size", required = false, defaultValue = "5") @Min(value = 5) @Max(value = 20) Integer pageSize,
            @RequestParam(value = "sort", required = false, defaultValue = "code") String sortField

    ) throws BadRequestException {

        if(!propertyMap.containsKey(sortField)) {
           throw new BadRequestException("Invalid sort field : " + sortField);
        }

        Page<Location> page = service.listByPage(pageNum - 1, pageSize, propertyMap.get(sortField));

        List<Location> locations = page.getContent();

        if (locations.isEmpty()) {
           return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listEntity2ListDTO(locations));
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

    private List<LocationDto> listEntity2ListDTO(List<Location> listEntity) {

        return listEntity.stream().map(this::entity2DTO)
                .collect(Collectors.toList());

    }

    private LocationDto entity2DTO(Location entity) {
        return modelMapper.map(entity, LocationDto.class);
    }

    private Location dto2Entity(LocationDto dto) {
        return modelMapper.map(dto, Location.class);
    }

}
