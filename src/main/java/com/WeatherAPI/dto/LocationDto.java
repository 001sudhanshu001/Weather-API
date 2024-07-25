package com.WeatherAPI.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"code", "city_name", "region_name", "country_code", "country_name", "enabled"})
public class LocationDto {

    @NotNull(message = "Location Code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must have 3 to 12 characters")
    private String code;

    @NotNull(message = "City name can't be null")
    @Length(min = 3, max = 128, message = "City name must have 3 to 128 characters")
    @JsonProperty("city_name")
    private String cityName;

    @NotNull(message = "Region name can't be null")
    @Length(min = 3, max = 128, message = "Region name must have 3 to 128 characters")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("region_name")
    private String regionName;

    @NotNull(message = "Country name can't be null")
    @Length(min = 3, max = 64, message = "Country name must have 3 to 64 characters")
    @JsonProperty("country_name")
    private String countryName;

    @NotNull(message = "Country code can't be null")
    @Length(min = 2, max = 2, message = "Country code must have 2 characters")
    @JsonProperty("country_code")
    private String countryCode;

    private boolean enabled;
}
