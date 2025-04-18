package com.WeatherAPI.dao;

import com.WeatherAPI.entity.RealTimeWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RealTimeWeatherRepo extends JpaRepository<RealTimeWeather, String> {

    // There may be possibility that a city with a name is in two countries, so we used both city and country
    @Query("SELECT r FROM RealTimeWeather r WHERE r.location.countryCode = ?1 AND r.location.cityName = ?2")
    Optional<RealTimeWeather> findByCountryCodeAndCity(String countryCode, String city);

    @Query("SELECT r FROM RealTimeWeather r WHERE r.locationCode = ?1 AND r.location.trashed = false")
    Optional<RealTimeWeather> findByLocationCode(String locationCode);
}
