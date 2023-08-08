package com.WeatherAPI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "Location Code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must have 3 to 12 characters")
    private String code;

    @Column(length = 128, nullable = false)
    @NotNull(message = "City name can't be null")
    @Length(min = 3, max = 128, message = "City name must have 3 to 128 characters")
    @JsonProperty("city_name")
    private String cityName;

    @Column(length = 128)
    @NotNull(message = "Region name can't be null")
    @Length(min = 3, max = 128, message = "Region name must have 3 to 128 characters")
    @JsonProperty("region_name")
    private String regionName;
    @Column(length = 64, nullable = false)
    @NotNull(message = "Country name can't be null")
    @Length(min = 3, max = 64, message = "Country name must have 3 to 64 characters")
    @JsonProperty("country_name")
    private String countryName;
    @Column(length = 2, nullable = false)
    @NotNull(message = "Country code can't be null")
    @Length(min = 2, max = 2, message = "Country code must have 2 characters")
    @JsonProperty("country_code")
    private String countryCode;

    private boolean enabled;
    @JsonIgnore // This won't be sent into JSON
    private boolean trashed;
    // Locations will not be deleted permanently , they will be marked as trashed if needed

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private RealTimeWeather realTimeWeather;

    @OneToMany(mappedBy = "id.location")
    private List<HourlyWeather> hourlyWeatherList = new ArrayList<>();

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return  cityName + ", " + (regionName != null ? regionName + "," : "") + countryName;
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
}


