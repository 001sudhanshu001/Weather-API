package com.WeatherAPI.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "weather_daily")
@Getter @Setter
public class DailyWeather {

    @EmbeddedId
    private DailyWeatherId id = new DailyWeatherId();

    private int minTemp;

    private int maxTemp;

    private int precipitation;

    @Column(length = 50)
    private String status;

    // Builder
    public DailyWeather precipitation(int precipitation){
        setPrecipitation(precipitation);
        return this;
    }
    public DailyWeather status(String status){
        setStatus(status);
        return this;
    }
    public DailyWeather location(Location location){
        this.id.setLocation(location);
        return this;
    }

    public DailyWeather dayOfMonth(int day) {
        this.id.setDayOfMonth(day);
        return this;
    }

    public DailyWeather month(int month) {
        this.id.setMonth(month);
        return this;
    }

    public DailyWeather minTemp(int minTemp) {
        setMinTemp(minTemp);
        return this;
    }

    public DailyWeather maxTemp(int maxTemp) {
        setMaxTemp(maxTemp);
        return this;
    }
}
