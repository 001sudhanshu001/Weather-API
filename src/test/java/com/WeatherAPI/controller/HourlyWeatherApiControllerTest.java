package com.WeatherAPI.controller;

import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(HourlyWeatherApiController.class)
class HourlyWeatherApiControllerTest {
    private static final String X_CURRENT_HOUR = "X-Current-Hour";
    private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";
    private static final String REQUEST_CONTENT_TYPE = "application/json";
    private static final String END_POINT_PATH = "/v1/hourly";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HourlyWeatherService hourlyWeatherService;

    @MockBean
    private GeoLocationService geoLocationService;

    @Test
    public void testGetIpShouldReturn404BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest()).andDo(print());

    }

    @Test
    public void testGetIpShouldReturn400BadRequestBecauseGeoLocationException() throws Exception {
        when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenThrow(GeoLocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isBadRequest()).andDo(print());

    }

    @Test
    public void testGetIpShouldReturn204NoContent() throws Exception {
        int currentHour = 9;

        Location location = new Location().code("MUB");
        when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);

        // TODO -> Use HourlyWeatherListDto
//        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent()).andDo(print());

    }

    @Test
    public void testGetIpShouldReturn200OK() throws Exception {
        int currentHour = 9;

        Location location = new Location();
        location.setCode("MUB");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);


        List<HourlyWeather> hourlyWeatherList = location.getHourlyWeatherList();

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(28)
                .precipitation(40)
                .status("cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(12)
                .temperature(30)
                .precipitation(45)
                .status("cloudy");

        hourlyWeatherList.add(forecast1);
        hourlyWeatherList.add(forecast2);

        when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);

        // TODO -> User HourlyWeatherListDto
//        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(hourlyWeatherList);

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location", is(expectedLocation)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hourly_forecast[0].hour_of_day", is(11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", is("http://localhost/v1/hourly")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily")))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full")))
                .andDo(print());

    }

    @Test
    public void testGetByCodeShouldReturn400BadRequest() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        int currentHour = 9;
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(ex);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        // TODO -> Use DTO after code refactor
//        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        int currentHour = 9;
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Delhi");
        location.setRegionName("Delhi");
        location.setCountryCode("IN");
        location.setCountryName("India");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        var hourlyForecast = List.of(forecast1, forecast2);

        // TODO -> Use DTO after code refactor
//        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(hourlyForecast);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/hourly/" + locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/MUB";

        List<HourlyWeatherDto> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/MUB";

        HourlyWeatherDto dto1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(133) // invalid data
                .precipitation(70)
                .status("Cloudy");

        HourlyWeatherDto dto2 = new HourlyWeatherDto()
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        List<HourlyWeatherDto> listDTO = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in the range")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDto dto1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        List<HourlyWeatherDto> listDTO = List.of(dto1);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
                .thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDto dto1 = new HourlyWeatherDto()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeatherDto dto2 = new HourlyWeatherDto()
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        List<HourlyWeatherDto> listDTO = List.of(dto1, dto2);

        var hourlyForecast = List.of(forecast1, forecast2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
                .thenReturn(hourlyForecast);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/hourly/" + locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href", is("http://localhost/v1/realtime/" + locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }

}