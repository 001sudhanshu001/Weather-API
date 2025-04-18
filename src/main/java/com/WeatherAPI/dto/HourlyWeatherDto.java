package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;


@Getter @Setter
@JsonPropertyOrder({"hour_of_day", "temperature", "precipitation", "status"})
@ToString
public class HourlyWeatherDto implements Serializable {
    @JsonProperty("hour_of_day")
    @Range(min = 0, max = 23, message = "Hour of day must be in between 0-23")
    private int hourOfDay;

    @Range(min = -50, max = 50, message = "Temperature must be in the range -50 to 50 degree Celsius")
    private int temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range 0 to 100%")
    private int precipitation;

    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between in 3 to 50 characters")
    private String status;


    // Builders
    public HourlyWeatherDto precipitation(int precipitation){
        setPrecipitation(precipitation);
        return this;
    }
    public HourlyWeatherDto status(String status){
        setStatus(status);
        return this;
    }
    public HourlyWeatherDto hourOfDay(int hour){
        setHourOfDay(hourOfDay);
        return this;
    }

    public HourlyWeatherDto temperature(int temp){
        setTemperature(temp);
        return this;
    }
}
