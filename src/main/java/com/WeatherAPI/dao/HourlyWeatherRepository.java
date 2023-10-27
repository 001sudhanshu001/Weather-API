package com.WeatherAPI.dao;

import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.HourlyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HourlyWeatherRepository extends JpaRepository<HourlyWeather, HourlyWeatherId> {

    // Return hourly weather for upcoming hours
    @Query("""
			SELECT h FROM HourlyWeather h WHERE
			h.id.location.code = ?1 AND h.id.hourOfDay > ?2
			AND h.id.location.trashed = false
			""")
    List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}