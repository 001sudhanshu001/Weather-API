package com.WeatherAPI.dto;

import com.WeatherAPI.filter.RealTimeWeatherFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class FullWeatherDTO {
    private String location;

    // make sure the variable name are same as in Location.class
    @JsonProperty("realtime_weather")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RealTimeWeatherFilter.class)
    private RealTimeWeatherDto realTimeWeather = new RealTimeWeatherDto();

    @JsonProperty("hourly_forecast")
    @Valid
    private List<HourlyWeatherDto> hourlyWeatherList = new ArrayList<>();

    @JsonProperty("daily_forecast")
    @Valid
    private List<DailyWeatherDTO> dailyWeather = new ArrayList<>();

}
