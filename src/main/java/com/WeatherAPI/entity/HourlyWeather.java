package com.WeatherAPI.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "weather_hourly")
@Getter @Setter
@NoArgsConstructor
public class HourlyWeather {
    @EmbeddedId  // Composite id
    private HourlyWeatherId id = new HourlyWeatherId();

    private int temperature;
    private int precipitation;
    @Column(length = 50)
    private String status;

    // Builder
    public HourlyWeather temperature(int temp){
        setTemperature(temp);
        return this;
    }
    public HourlyWeather precipitation(int precipitation){
        setPrecipitation(precipitation);
        return this;
    }
    public HourlyWeather status(String status){
        setStatus(status);
        return this;
    }
    public HourlyWeather hourOfDay(int hour){
        this.id.setHourOfDay(hour);
        return this;
    }
    public HourlyWeather location(Location location){
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather id(Location location, int hour){
        this.id.setHourOfDay(hour);
        this.id.setLocation(location);

        return this;
    }

}
