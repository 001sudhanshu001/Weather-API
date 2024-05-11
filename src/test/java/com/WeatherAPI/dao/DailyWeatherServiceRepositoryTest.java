package com.WeatherAPI.dao;

import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.DailyWeatherId;
import com.WeatherAPI.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class DailyWeatherServiceRepositoryTest {

    @Autowired
    private DailyWeatherRepository dailyWeatherRepository;

    @Test
    public void addTest() {
        String locationCode = "MUB";

        Location location = new Location().code(locationCode);

        DailyWeather forecast = new DailyWeather()
                .location(location)
                .dayOfMonth(18)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather addedForecast = dailyWeatherRepository.save(forecast);


        assertThat(addedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
    }

    @Test
    public void testDelete() {
        String locationCode = "MUB";

        Location location = new Location().code(locationCode);

        DailyWeatherId id = new DailyWeatherId(18, 7, location);

        dailyWeatherRepository.deleteById(id);

        Optional<DailyWeather> result = dailyWeatherRepository.findById(id);

        assertThat(result).isNotPresent();
    }

}
