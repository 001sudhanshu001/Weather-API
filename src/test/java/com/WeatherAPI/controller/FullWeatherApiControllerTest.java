package com.WeatherAPI.controller;

import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.RealTimeWeatherDto;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.FullWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherApiController.class)
public class FullWeatherApiControllerTest {
    private static final String END_POINT_PATH = "/v1/full";
    private static final String REQUEST_CONTENT_TYPE = "application/json";
    private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FullWeatherService fullWeatherService;

    @MockBean
    private GeoLocationService geoLocationService;

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        GeoLocationException ex = new GeoLocationException("Geolocation error");
        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }


    @Test
    public void testGetByIPShouldReturn404NotFound() throws Exception {
        Location location = new Location().code("DELHI_IN");

        when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(fullWeatherService.getLocation(location)).thenThrow(ex);
        // LocationNotFoundException is handled in GlobalExceptionHandler to give 404

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        RealTimeWeather realtimeWeather = new RealTimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        location.setRealTimeWeather(realtimeWeather);

        DailyWeather hourlyForecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather hourlyForecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        location.setDailyWeather(List.of(hourlyForecast1, hourlyForecast2));

        HourlyWeather hourlyWeather1 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(28)
                .precipitation(40)
                .status("cloudy");

        HourlyWeather hourlyWeather2 = new HourlyWeather()
                .location(location)
                .hourOfDay(12)
                .temperature(30)
                .precipitation(45)
                .status("cloudy");

        location.setHourlyWeatherList(List.of(hourlyWeather1, hourlyWeather2));

        when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);

        // TODO -> Update to use DTO
//        when(fullWeatherService.getLocation(location)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(11)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/full")))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "ABC123";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherService.getLocationByCode(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Mumbai");
        location.setRegionName("India");
        location.setCountryCode("IN");
        location.setCountryName("India");

        RealTimeWeather realtimeWeather = new RealTimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        location.setRealTimeWeather(realtimeWeather);

        DailyWeather dailyForecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather dailyForecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        location.setDailyWeather(List.of(dailyForecast1, dailyForecast2));

        HourlyWeather hourlyForecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather hourlyForecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        location.setHourlyWeatherList(List.of(hourlyForecast1, hourlyForecast2));

        // TODO -> Update to use DTO
//        when(fullWeatherService.getLocationByCode(locationCode)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoHourlyWeather() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Hourly weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoDailyWeather() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecast1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecast1);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Daily weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidRealtimeWeatherData() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecast1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        fullWeatherDTO.getDailyWeather().add(dailyForecast1);

        RealTimeWeatherDto realtimeDTO = new RealTimeWeatherDto();
        realtimeDTO.setTemperature(122);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealTimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in the range")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidHourlyWeatherData() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecast1 = new HourlyWeatherDto()
                .hourOfDay(100)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        fullWeatherDTO.getDailyWeather().add(dailyForecast1);

        RealTimeWeatherDto realtimeDTO = new RealTimeWeatherDto();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealTimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Hour of day must be in between")))
                .andDo(print());
    }


    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidDailyWeatherData() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecast1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("");

        fullWeatherDTO.getDailyWeather().add(dailyForecast1);

        RealTimeWeatherDto realtimeDTO = new RealTimeWeatherDto();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealTimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Status must be in between")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "MUB123";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecast1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        fullWeatherDTO.getDailyWeather().add(dailyForecast1);

        RealTimeWeatherDto realtimeDTO = new RealTimeWeatherDto();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealTimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherService.update(Mockito.eq(locationCode), Mockito.any())).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }


    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");

        RealTimeWeather realtimeWeather = new RealTimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        location.setRealTimeWeather(realtimeWeather);

        DailyWeather dailyForecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        location.setDailyWeather(List.of(dailyForecast1));

        HourlyWeather hourlyForecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");


        location.setHourlyWeatherList(List.of(hourlyForecast1));

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDto hourlyForecastDTO1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        fullWeatherDTO.getHourlyWeatherList().add(hourlyForecastDTO1);

        DailyWeatherDTO dailyForecastDTO1 = new DailyWeatherDTO()
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        fullWeatherDTO.getDailyWeather().add(dailyForecastDTO1);

        RealTimeWeatherDto realtimeDTO = new RealTimeWeatherDto();
        realtimeDTO.setTemperature(12);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealTimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        when(fullWeatherService.update(Mockito.eq(locationCode), Mockito.any())).thenReturn(location);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }
}
