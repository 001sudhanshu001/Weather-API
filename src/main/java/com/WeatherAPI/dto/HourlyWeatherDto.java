package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonPropertyOrder({"hour_of_day", "temperature", "precipitation", "status"})
public class HourlyWeatherDto {
    @JsonProperty("hour_of_day")
    private int hourOfDay;
    private int temperature;
    private int precipitation;
    private String status;
}
