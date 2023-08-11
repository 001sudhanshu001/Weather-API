package com.WeatherAPI.controller;

import com.WeatherAPI.service.GeoLocationService;
import com.WeatherAPI.service.HourlyWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.jboss.logging.MDC.get;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(HourlyWeatherApiController.class)
class HourlyWeatherApiControllerTest {
    private static final String END_POINT_PATH = "/v1/hourly";

    @Autowired private MockMvc mockMvc;
    @Autowired private HourlyWeatherService weatherService;
    @Autowired private GeoLocationService geoLocationService;

//    @Test
//    public void testGetIpShouldReturn404BadRequestBecauseNoHeaderXCurrentHour() {
//     //   mockMvc.perform(get(END_POINT_PATH)).andExpect(s)
//    }

}