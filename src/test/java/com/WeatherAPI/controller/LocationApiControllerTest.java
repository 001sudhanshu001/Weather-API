package com.WeatherAPI.controller;

import com.WeatherAPI.dto.LocationDto;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.LocationNotFoundException;
import com.WeatherAPI.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTest {
    private static final String END_POINT_PATH = "/v1/locations";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean LocationService service;

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception {
        LocationDto location = new LocationDto();

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testShouldReturn201Created() throws Exception {
        Location location = new Location();
        location.setCode("CHD3");
        location.setCityName("Chandigarh");
        location.setRegionName("Chandigarh");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        LocationDto dto = new LocationDto();
        dto.setCode(location.getCode());
        dto.setCityName(location.getCityName());
        dto.setRegionName(location.getRegionName());
        dto.setCountryCode(location.getCountryCode());
        dto.setCountryName(location.getCountryName());
        dto.setEnabled(location.isEnabled());

        Mockito.when(service.addLocation(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(dto);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.code", is("CHD1")))
                .andExpect(header().string("location", "/v1/location/CHD3"))
                .andDo(print());
    }

    @Test
    public void testListShouldRequestBodyLocationCode() throws Exception{
        Location location = new Location();
        location.setCityName("Chandigarh");
        location.setRegionName("Chandigarh");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Mockito.when(service.addLocation(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    public void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        LocationDto location = new LocationDto();
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.errors[0]", is("Location code cannot be null")))
                .andDo(print());
    }

    @Test
    public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
        LocationDto location = new LocationDto();
        location.setRegionName("");

        String bodyContent = mapper.writeValueAsString(location);

        MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("Location Code cannot be null");
        assertThat(responseBody).contains("City name can't be null");
        assertThat(responseBody).contains("Region name must have 3 to 128 characters");
        assertThat(responseBody).contains("Country name can't be null");
        assertThat(responseBody).contains("Country code can't be null");
    }

    @Disabled
    @Test
    public void testListShouldReturn204NoContent() throws Exception {
        Mockito.when(service.list()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Disabled
    @Test
    public void testListShouldReturn200() throws Exception {
        LocationDto location1 = new LocationDto();
        location1.setCode("SMK");
        location1.setCityName("Samalkha");
        location1.setRegionName("Panipat");
        location1.setCountryCode("IN");
        location1.setCountryName("India");
        location1.setEnabled(true);

        LocationDto location2 = new LocationDto();
        location2.setCode("PNP");
        location2.setCityName("Panipat");
        location2.setRegionName("NCR");
        location2.setCountryCode("IN");
        location2.setCountryName("India");
        location2.setEnabled(true);

        Mockito.when(service.list()).thenReturn(List.of(location1, location2));

        mockMvc.perform(get(END_POINT_PATH).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.code", is("CHD1")))
                .andDo(print());
    }

    @Test
    public void testListByPageShouldReturn204NoContent() throws Exception {

        Mockito.when(service.listByPage(anyInt(), anyInt(), anyString()))
                .thenReturn(Page.empty());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testListByPageShouldReturn200OK() throws Exception {
        Location location1 = new Location();
        location1.setCode("MUB");
        location1.setCityName("Mumbai");
        location1.setRegionName("Maharashtra");
        location1.setCountryCode("IN");
        location1.setCountryName("India");
        location1.setEnabled(true);

        Location location2 = new Location();
        location2.setCode("DELHI_IN");
        location2.setCityName("Delhi");
        location2.setRegionName("Delhi");
        location2.setCountryCode("In");
        location2.setCountryName("India");
        location2.setEnabled(true);

        List<Location> listLocations = List.of(location1, location2);

        int pageSize = 5;
        int pageNum = 1;
        String sortField = "code";
        int totalElements = listLocations.size();

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        PageImpl<Location> page = new PageImpl<>(listLocations, pageable, totalElements);

        Mockito.when(service.listByPage(pageNum - 1, pageSize, sortField))
                .thenReturn(page);

        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.locations[0].code", is("MUB")))
                .andExpect(jsonPath("$._embedded.locations[0].city_name", is("Mumbai")))
                .andExpect(jsonPath("$._embedded.locations[1].code", is("DELHI_IN")))
                .andExpect(jsonPath("$._embedded.locations[1].city_name", is("Delhi")))
                .andExpect(jsonPath("$.page.size", is(pageSize)))
                .andExpect(jsonPath("$.page.number", is(pageNum)))
                .andExpect(jsonPath("$.page.total_elements", is(totalElements)))
                .andExpect(jsonPath("$.page.total_pages", is(1)))
                .andDo(print());
    }

    @Test
    public void testListByPageShouldReturn400BadRequestInvalidPageNum() throws Exception {
        int pageNum = 0;
        int pageSize = 5;
        String sortField = "code";

        Mockito.when(service.listByPage(anyInt(), anyInt(), anyString()))
                .thenReturn(Page.empty());

        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("must be greater than or equal to 1")))
                .andDo(print());
    }

    @Test
    public void testListByPageShouldReturn400BadRequestInvalidPageSize() throws Exception {
        int pageNum = 1;
        int pageSize = 3;
        String sortField = "code";

        Mockito.when(service.listByPage(anyInt(), anyInt(), anyString()))
                .thenReturn(Page.empty());

        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("must be greater than or equal to 5")))
                .andDo(print());
        // This message is the Default of @Min() from jakarta.validation.constraints.Min.message = must be greater than or equal to {value}
    }

    @Test
    public void testListByPageShouldReturn400BadRequestInvalidSortField() throws Exception {
        int pageNum = 1;
        int pageSize = 5;
        String sortField = "code_abc";

        Mockito.when(service.listByPage(anyInt(), anyInt(), anyString()))
                .thenReturn(Page.empty());

        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsStringIgnoringCase("invalid sort field")))
                .andDo(print());
    }



    @Test
    public void testGetShouldReturn405MethodNotAllowed() throws Exception {
        String requestURI = END_POINT_PATH + "/ABCDE";

        mockMvc.perform(post(requestURI))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404NotFound() throws Exception {
        String code = "MUB5";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(service).get(code);


        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    public void testGetShouldReturn200OK() throws Exception {
        String locationCode = "MUB";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationDto location = new LocationDto();
        location.setCode("MUB");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Mockito.when(service.get(locationCode)).thenReturn(location);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        LocationDto location = new LocationDto();
        location.setCode("MUB1");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Mockito.when(service.update(Mockito.any())).thenThrow(new LocationNotFoundException("No Location Found with the code"));
        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseLocationCodeCanNotBeNull() throws Exception {
        LocationDto location = new LocationDto();
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("MUB");
        location.setCityName("Amchi Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        LocationDto dto = new LocationDto();
        dto.setCode(location.getCode());
        dto.setCityName(location.getCityName());
        dto.setRegionName(location.getRegionName());
        dto.setCountryCode(location.getCountryCode());
        dto.setCountryName(location.getCountryName());
        dto.setEnabled(location.isEnabled());

        Mockito.when(service.update(location)).thenReturn(location);
        String bodyContent = mapper.writeValueAsString(dto);

        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk() )
                .andDo(print());
    }

    @Test
    public void testDeleteShouldReturn404NotFound() throws Exception {
        String code = "MUB5";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteShouldReturn204NoContent() throws Exception {
        String code = "MUB";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(service).delete(code);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

}
