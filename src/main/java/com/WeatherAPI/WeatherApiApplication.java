package com.WeatherAPI;

import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApiApplication.class, args);
	}
//	@Bean
//	public ModelMapper modelMapper(){
//		ModelMapper modelMapper = new ModelMapper();
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//		// because we are using embedded object so to map hourOfDay of embedded object to the DTO :
//		var typeMap1 = modelMapper.typeMap(HourlyWeather.class, HourlyWeatherDto.class);
//		typeMap1.addMapping(src-> src.getId().getHourOfDay(), HourlyWeatherDto::setHourOfDay);
//
//		//
//		var typeMap2 = modelMapper.typeMap(HourlyWeatherDto.class, HourlyWeather.class);
//		typeMap2.addMapping(src-> src.getHourOfDay(),
//				(dest,value) -> dest.getId().setHourOfDay(value != null ? (int)value : 0));
//
//		return modelMapper;
//	}

	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDto.class);
		typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDto::setHourOfDay);

		var typeMap2 = mapper.typeMap(HourlyWeatherDto.class, HourlyWeather.class);
		typeMap2.addMapping(src -> src.getHourOfDay(),
				(dest, value) ->	dest.getId().setHourOfDay(value != null ? (int) value : 0));

		var typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
		typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth);
		typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

		return mapper;
	}

}
