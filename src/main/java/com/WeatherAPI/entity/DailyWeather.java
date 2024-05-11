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

}
