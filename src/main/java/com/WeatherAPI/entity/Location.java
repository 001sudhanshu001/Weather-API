package com.WeatherAPI.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "locations", indexes = {
    @Index(name = "city_name_index", columnList = "cityName")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class   Location {

    @Column(length = 12, nullable = false, unique = true)
    @Id
    private String code;

    @Column(length = 128, nullable = false)
    @NotNull(message = "City name can't be null")
    private String cityName;

    @Column(length = 128)
    @NotNull(message = "Region name can't be null")
    private String regionName;

    @Column(length = 64, nullable = false)
    private String countryName;

    @Column(length = 2, nullable = false)
    private String countryCode;

    private boolean enabled;

    private boolean trashed;
    // Locations will not be deleted permanently , they will be marked as trashed if needed

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private RealTimeWeather realTimeWeather;

    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true) // One Location will have weather info for more than one hour
    private List<HourlyWeather> hourlyWeatherList = new ArrayList<>();

    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DailyWeather> dailyWeather = new ArrayList<>();

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return  cityName + ", " + (regionName != null ? regionName + ", " : "") + countryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return Objects.equals(code,location.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    // Builder
    public Location code(String code){
        setCode(code);
        return this;
    }
}
