package com.WeatherAPI.dao.filter;

import com.WeatherAPI.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface FilterableLocationRepository {
    Page<Location> listWithFilter(Pageable pageable, Map<String, Object> filterFields);

}
