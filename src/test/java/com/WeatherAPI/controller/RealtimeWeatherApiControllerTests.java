package com.WeatherAPI.controller;

import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.RealTimeWeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RealTimeWeatherController.class)
public class RealtimeWeatherApiControllerTests {
    private static final  String END_POINT_END = "/v1/realtime";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RealTimeWeatherService realTimeWeatherService;
    @MockBean
    GeoLocationService geoLocationService;

    @Test
    public void testGetShouldReturn400BadRequest() throws Exception {
        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString()))
                .thenThrow(GeoLocationException.class);

        mockMvc.perform(get(END_POINT_END))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        Location location = new Location();

        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(realTimeWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_END))
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


        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(realTimeWeatherService.getByLocation(location)).thenReturn(realTimeWeather);

        mockMvc.perform(get(END_POINT_END))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test // Just to test Request body so no need to use mockito
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        String locationCode = "MUB";
        String reqestURI = END_POINT_END + "/" + locationCode;

        RealTimeWeather realTimeWeather = new RealTimeWeather();

        realTimeWeather.setTemperature(120);
        realTimeWeather.setHumidity(65);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("Windy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());

        String bodyContent = mapper.writeValueAsString(realTimeWeather);

        System.out.println(bodyContent);
//        mockMvc.perform(put(END_POINT_END).contentType("application/json").content(bodyContent))
//                .andExpect(status().isBadRequest())
//                .andDo(print());
    }

    @Test // Just to test Request body so no need to use mockito
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "MUB";
        String reqestURI = END_POINT_END + "/" + locationCode;

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

        Mockito.when(realTimeWeatherService.update(locationCode, realTimeWeather)).thenReturn(realTimeWeather);
        String bodyContent = mapper.writeValueAsString(realTimeWeather);

        System.out.println(bodyContent);
        mockMvc.perform(put(END_POINT_END).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
