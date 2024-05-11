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
    @JsonIgnore
    private String locationCode;

    @Range(min = -50, max = 50, message = "Temperature must be in the range -50 to 50 degree Celsius")
    private int temperature;

    @Range(min = 0, max = 100, message = "Humidity must be in the range 0 to 100%")
    private int humidity;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range 0 to 100%")
    int precipitation;

    @Column(length = 50)
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3,  max = 50, message = "Status must be in between in 3 to 50 characters")
    private String status;

    @JsonProperty("wind_speed")
    @Range(min = 0, max = 200, message = "Wind Speed must be in the range 0 to 200 km/h")
    private int windSpeed;

    @JsonProperty("last_updated")
    @JsonIgnore
    private Date lastUpdated;

    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId  // To make same id as Location
    @JsonIgnore
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
