package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Getter
@Setter
@ToString
@JsonPropertyOrder({"day_of_month", "month", "min_temp", "max_temp", "precipitation", "status"})
public class DailyWeatherDTO {

    @Range(min = 1, max = 31, message = "Day of month must be between 1-31")
    @JsonProperty("day_of_month")
    private int dayOfMonth;

    @Range(min = 1, max = 12, message = "Month must be between 1-12")
    private int month;

    @Range(min = -50, max = 50, message = "Minimum temperature must be in the range of -50 to 50 Celsius degree")
    @JsonProperty("min_temp")
    private int minTemp;

    @Range(min = -50, max = 50, message = "Maximum temperature must be in the range of -50 to 50 Celsius degree")
    @JsonProperty("max_temp")
    private int maxTemp;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;

    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;

    public DailyWeatherDTO dayOfMonth(int day) {
        setDayOfMonth(day);
        return this;
    }

    public DailyWeatherDTO month(int month) {
        setMonth(month);
        return this;
    }

    public DailyWeatherDTO minTemp(int temp) {
        setMinTemp(temp);
        return this;
    }

    public DailyWeatherDTO maxTemp(int temp) {
        setMaxTemp(temp);
        return this;
    }

    public DailyWeatherDTO precipitation(int precipitation) {
        setPrecipitation(precipitation);
        return this;
    }

    public DailyWeatherDTO status(String status) {
        setStatus(status);
        return this;
    }
}
