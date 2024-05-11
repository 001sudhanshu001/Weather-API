package com.WeatherAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "realtime_weather")
@Getter @Setter
public class RealTimeWeather {
    @Id
    @Column(name = "location_code")
    private String locationCode;

    private int temperature;

    private int humidity;

    int precipitation;

    @Column(length = 50, nullable = false)
    private String status;

    private int windSpeed;

    private Date lastUpdated;

    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId  // To make same id as Location
    private Location location;

    public void setLocation(Location location){
        this.locationCode = location.getCode();
        this.location = location;
    }

    @Override
    public String toString() {
        return "RealTimeWeather{" +
                "locationCode='" + locationCode + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", status='" + status + '\'' +
                ", windSpeed=" + windSpeed +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealTimeWeather that = (RealTimeWeather) o;

        return Objects.equals(locationCode, that.locationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locationCode);
    }
}
