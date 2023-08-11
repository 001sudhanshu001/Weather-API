package com.WeatherAPI.dao;

import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.HourlyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourlyWeatherRepository extends JpaRepository<HourlyWeather, HourlyWeatherId> {
}