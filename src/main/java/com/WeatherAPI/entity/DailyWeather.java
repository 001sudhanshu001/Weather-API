package com.WeatherAPI.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "weather_daily")
@Getter @Setter
public class DailyWeather implements Serializable {

    @EmbeddedId
    private DailyWeatherId id = new DailyWeatherId();

    private int minTemp;

    private int maxTemp;

    private int precipitation;

    @Column(length = 50)
    private String status;

    // getShallowCopy and equals method are used to check the equality of object while updating DailyWeather
    public DailyWeather getShallowCopy() {
        DailyWeather copy = new DailyWeather();
        copy.setId(this.getId());

        return copy;
    }

    @Override
    public String toString() {
        return "DailyWeather [id=" + id + ", minTemp=" + minTemp + ", maxTemp=" + maxTemp + ", precipitation="
                + precipitation + ", status=" + status + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DailyWeather other = (DailyWeather) obj;
        return Objects.equals(id, other.id);
    }

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
