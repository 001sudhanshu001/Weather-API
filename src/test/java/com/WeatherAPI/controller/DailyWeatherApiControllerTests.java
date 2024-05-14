package com.WeatherAPI.controller;

import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.DailyWeatherService;
import com.WeatherAPI.service.GeoLocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DailyWeatherApiController.class)
public class DailyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/daily";
    private static final String REQUEST_CONTENT_TYPE = "application/json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DailyWeatherService dailyWeatherService;

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
        when(dailyWeatherService.getByLocation(location)).thenThrow(ex);
        // GeoLocationException is handled in GlobalExceptionHandler to give 400

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {
        Location location =  new Location().code("DELHI");

        Mockito.when(geoLocationService.getLocationFromIpAddress(anyString())).thenReturn(location);
        Mockito.when(dailyWeatherService.getByLocation(location)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
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

        DailyWeather forecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather forecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }


    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn204NoContent() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        DailyWeather forecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather forecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Rainy");

        when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }


    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        List<DailyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Daily forecast data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        DailyWeatherDTO dto1 = new DailyWeatherDTO()
                .dayOfMonth(40)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear");

        DailyWeatherDTO dto2 = new DailyWeatherDTO()
                .dayOfMonth(20)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear");

        List<DailyWeatherDTO> listDTO = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("Day of month must be between 1-31")))
                .andDo(print());

    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        DailyWeatherDTO dto = new DailyWeatherDTO()
                .dayOfMonth(21)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear");

        List<DailyWeatherDTO> listDTO = List.of(dto);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType(REQUEST_CONTENT_TYPE).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());

    }
}
