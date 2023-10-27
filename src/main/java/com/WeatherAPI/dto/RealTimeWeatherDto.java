package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter @Setter
public class RealTimeWeatherDto {

    // ModelMapper map Location(reference) of RealTimeWeather to this location(String)
    // Because they have same name, Since we can't directly map reference to String
    // So we override the toString() method Location
    private String location;
    private int temperature;
    private int humidity;
    int precipitation;
    private String status;
    @JsonProperty("wind_speed")
    private int windSpeed;
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;

    @Override
    public String toString() {
        return "RealTimeWeather{" +
                "locationCode='" + location + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                ", windSpeed=" + windSpeed +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
