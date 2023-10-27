package com.WeatherAPI.controller;

import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.CodeConflictException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationApiController {
    private final LocationService service;

    @PostMapping
    public ResponseEntity<?>  addLocation(@RequestBody @Valid Location location){
        Location addedLocation;
        try {
            addedLocation = service.addLocation(location);
        } catch (CodeConflictException e) {
            return new ResponseEntity<>("This code is already used for other city",HttpStatus.CONFLICT);
        }
        URI uri = URI.create("/v1/location/" + addedLocation.getCode());
        return ResponseEntity.created(uri).body(addedLocation);
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<Location> locations = service.list();

        if(locations.isEmpty()){
            return ResponseEntity.noContent().build(); // status code 204
        }
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable("code") String code){
        Location location = service.get(code);

        if(location == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    @PutMapping
    public ResponseEntity<?> updateByCode(@RequestBody @Valid Location location){
        try {
            Location updatedLocation = service.update(location);
            return ResponseEntity.ok(updatedLocation);
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
        try {
            service.delete(code);
            return ResponseEntity.noContent().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
