package com.WeatherAPI;

import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.entity.HourlyWeather;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper(){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		// because we are using embadded object so to map hourOfDay of embadded object to the DTO :
		var typeMap = modelMapper.typeMap(HourlyWeather.class, HourlyWeatherDto.class);
		typeMap.addMapping(src-> src.getId().getHourOfDay(), HourlyWeatherDto::setHourOfDay);

		return modelMapper;
	}

}
