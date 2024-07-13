package com.WeatherAPI.filter;

import com.WeatherAPI.dto.RealTimeWeatherDto;

public class RealTimeWeatherFilter {
    // On returning false field will be included in the JSON on true it will be filtered out
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RealTimeWeatherFilter) {
            RealTimeWeatherDto dto = (RealTimeWeatherDto) obj;
            return dto.getStatus() == null;
        }
        return false;
    }
}
