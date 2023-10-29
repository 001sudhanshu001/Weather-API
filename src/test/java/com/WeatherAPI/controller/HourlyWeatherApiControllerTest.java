package com.WeatherAPI.controller;

import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(HourlyWeatherApiController.class)
class HourlyWeatherApiControllerTest {
    public static final String X_CURRENT_HOUR = "X-Current-Hour";
    private static final String END_POINT_PATH = "/v1/hourly";

    @Autowired private MockMvc mockMvc;
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
        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenThrow(GeoLocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isBadRequest()).andDo(print());

    }

    @Test
    public void testGetIpShouldReturn204NoContent() throws Exception {
        int currentHour = 9;

        Location location = new Location().code("MUB");
        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
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

        Mockito.when(geoLocationService.getLocationFromIpAddress(Mockito.anyString())).thenReturn(location);
        Mockito.when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(hourlyWeatherList);

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());

    }

}