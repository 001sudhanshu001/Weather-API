package com.WeatherAPI.dao;

import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.DailyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyWeatherRepository extends JpaRepository<DailyWeather, DailyWeatherId> {
    @Query("""
			SELECT d FROM DailyWeather d WHERE d.id.location.code = ?1
			AND d.id.location.trashed = false
			""")
    List<DailyWeather> findByLocationCode(String locationCode);
}
