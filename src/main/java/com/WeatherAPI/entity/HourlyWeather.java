package com.WeatherAPI.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "weather_hourly")
@Getter @Setter
public class HourlyWeather {
    @EmbeddedId
    private HourlyWeatherId id = new HourlyWeatherId();

    private int temperature;
    @Column(length = 50)
    private String status;

}
