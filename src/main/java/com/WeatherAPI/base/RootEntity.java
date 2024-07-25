package com.WeatherAPI.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;


@JsonPropertyOrder({"locations_url", "location_by_code_url", "realtime_weather_by_ip_url",
        "realtime_weather_by_code_url", "hourly_forecast_by_ip_url", "hourly_forecast_by_code_url",
        "daily_forecast_by_ip_url", "daily_forecast_by_code_url",
        "full_weather_by_ip_url", "full_weather_by_code_url"})
@Getter @Setter
public class RootEntity {

    @JsonProperty("locations_url")
    private String locationsUrl;

    @JsonProperty("location_by_code_url")
    private String locationByCodeUrl;

    @JsonProperty("realtime_weather_by_ip_url")
    private String realtimeWeatherByIpUrl;

    @JsonProperty("realtime_weather_by_code_url")
    private String realtimeWeatherByCodeUrl;

    @JsonProperty("hourly_forecast_by_ip_url")
    private String hourlyForecastByIpUrl;

    @JsonProperty("hourly_forecast_by_code_url")
    private String hourlyForecastByCodeUrl;

    @JsonProperty("daily_forecast_by_ip_url")
    private String dailyForecastByIpUrl;

    @JsonProperty("daily_forecast_by_code_url")
    private String dailyForecastByCodeUrl;

    @JsonProperty("full_weather_by_ip_url")
    private String fullWeatherByIpUrl;

    @JsonProperty("full_weather_by_code_url")
    private String fullWeatherByCodeUrl;

}