package com.WeatherAPI.dao;

import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.DailyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyWeatherRepository extends JpaRepository<DailyWeather, DailyWeatherId> {
}
