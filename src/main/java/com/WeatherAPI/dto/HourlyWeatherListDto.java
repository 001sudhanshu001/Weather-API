package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class HourlyWeatherListDto extends RepresentationModel<HourlyWeatherListDto> {
    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDto> hourlyForecast = new ArrayList<>();

    public void addWeatherHourlyDto(HourlyWeatherDto dto){
        this.hourlyForecast.add(dto);
    }
}
