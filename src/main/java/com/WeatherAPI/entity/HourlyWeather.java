package com.WeatherAPI.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "HourlyWeather{" +
                "hourOfDay=" + id.getHourOfDay() +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HourlyWeather that = (HourlyWeather) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    public HourlyWeather getShallowCopy() {
        HourlyWeather copy = new HourlyWeather();
        copy.setId(this.getId());

        return copy;
    }
}
