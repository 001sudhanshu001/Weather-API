package com.WeatherAPI.controller;

import com.WeatherAPI.dto.RealTimeWeatherDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.RealTimeWeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealTimeWeatherController.class)
public class RealtimeWeatherApiControllerTests {
    private static final  String END_POINT_PATH = "/v1/realtime";
    private static final String RESPONSE_CONTENT_TYPE = "application/hal+json";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    RealTimeWeatherService realTimeWeatherService;

    @MockBean
    GeoLocationService geoLocationService;

    @Test
    public void testGetShouldReturn400BadRequest() throws Exception {
        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString()))
                .thenThrow(GeoLocationException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        Location location = new Location();

        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(realTimeWeatherService.getWeatherByLocation(location)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("MUB");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        RealTimeWeather realTimeWeather = new RealTimeWeather();

        realTimeWeather.setTemperature(30);
        realTimeWeather.setHumidity(65);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("Windy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());
        realTimeWeather.setLocation(location);

        location.setRealTimeWeather(realTimeWeather);

        RealTimeWeatherDto realTimeWeatherDto = modelMapper.map(realTimeWeather, RealTimeWeatherDto.class);

        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(realTimeWeatherService.getWeatherByLocation(location)).thenReturn(realTimeWeatherDto);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime")))
                .andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly")))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily")))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full")))
                .andDo(print());
    }

    @Test // Just to test Request body so no need to use mockito for updateService
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealTimeWeatherDto realTimeWeather = new RealTimeWeatherDto();

        realTimeWeather.setTemperature(100); // invalid data
        realTimeWeather.setHumidity(65);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("Windy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());

        String bodyContent = mapper.writeValueAsString(realTimeWeather);

        System.out.println(bodyContent);
        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
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
        location.setEnabled(true);

        RealTimeWeather realTimeWeather = new RealTimeWeather();

        realTimeWeather.setTemperature(20);
        realTimeWeather.setHumidity(65);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("Windy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());
        realTimeWeather.setLocation(location);

        location.setRealTimeWeather(realTimeWeather);

        RealTimeWeatherDto dto = new RealTimeWeatherDto();
        dto.setTemperature(20);
        dto.setHumidity(65);
        dto.setPrecipitation(95);
        dto.setStatus("Windy");
        dto.setWindSpeed(40);
        dto.setLastUpdated(new Date());

        Mockito.when(realTimeWeatherService.update(locationCode, realTimeWeather)).thenReturn(realTimeWeather);

        String bodyContent = mapper.writeValueAsString(dto);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/v1/realtime/" + locationCode)))
                .andExpect(jsonPath("$._links.hourly_forecast.href", is("http://localhost/v1/hourly/" + locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href", is("http://localhost/v1/daily/" + locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href", is("http://localhost/v1/full/" + locationCode)))
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturnStatus404NotFound() throws Exception {
        String locationCode = "ABC_US";

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        Mockito.when(realTimeWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturnStatus200OK() throws Exception {
        String locationCode = "SFCA_USA";

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("San Franciso");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealTimeWeather realtimeWeather = new RealTimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        realtimeWeather.setLocation(location);
        location.setRealTimeWeather(realtimeWeather);

        RealTimeWeatherDto realTimeWeatherDto = modelMapper.map(realtimeWeather, RealTimeWeatherDto.class);
        Mockito.when(realTimeWeatherService.getByLocationCode(locationCode)).thenReturn(realTimeWeatherDto);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(RESPONSE_CONTENT_TYPE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }


}
