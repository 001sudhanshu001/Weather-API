package com.WeatherAPI;

import com.WeatherAPI.dto.DailyWeatherDTO;
import com.WeatherAPI.dto.FullWeatherDTO;
import com.WeatherAPI.dto.HourlyWeatherDto;
import com.WeatherAPI.dto.RealTimeWeatherDto;
import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
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
		// TODO : Code Refactor
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDto.class);
		typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDto::setHourOfDay);

		var typeMap2 = mapper.typeMap(HourlyWeatherDto.class, HourlyWeather.class);
		typeMap2.addMapping(src -> src.getHourOfDay(),
				(dest, value) ->	dest.getId().setHourOfDay(value != null ? (int) value : 0));

//		var typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
//		typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth);
//		typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

//		var typeMap4 = mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class);
//		typeMap4.addMapping(src -> src.getDayOfMonth(),
//						(dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0));
//
//		typeMap4.addMapping(src -> src.getMonth(),
//						(dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0));
		configureMappingForDailyWeather(mapper);

		var typeMap5 = mapper.typeMap(Location.class, FullWeatherDTO.class);
		typeMap5.addMapping(src -> src.toString(), FullWeatherDTO::setLocation);

		var typeMap6 = mapper.typeMap(RealTimeWeatherDto.class, RealTimeWeather.class);
		typeMap6.addMappings(m -> m.skip(RealTimeWeather::setLocation));

		return mapper;
	}

	private void configureMappingForDailyWeather(ModelMapper mapper) {
		mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class)
				.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth)
				.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

		mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class)
				.addMapping(src -> src.getDayOfMonth(),
						(dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0))

				.addMapping(src -> src.getMonth(),
						(dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0));
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		return objectMapper;
	}


}
