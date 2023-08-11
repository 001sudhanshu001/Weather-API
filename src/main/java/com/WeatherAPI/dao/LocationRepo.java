package com.WeatherAPI.dao;

import com.WeatherAPI.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepo extends JpaRepository<Location, String> {
    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    List<Location> findUntrashed();

    @Query("SELECT l FROM Location l WHERE l.trashed = false AND l.code = ?1")
    Location  findByCode(String code);

    @Query("UPDATE Location SET trashed = true WHERE code = ?1")
    @Modifying
    void trashedByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 AND l.trashed = false")
    Location findByCountryNameAndCityName(String countryCode, String cityName);


}
