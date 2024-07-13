package com.WeatherAPI.dao;

import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RealTimeWeatherRepoTest {
    @Autowired
    private RealTimeWeatherRepo repo;

    @Test
    public void testUpdate() {
        String code = "MUB";
        RealTimeWeather realTimeWeather = repo.findById(code).get();

        realTimeWeather.setTemperature(35);
        realTimeWeather.setHumidity(52);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("Windy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());

        RealTimeWeather updatedRealTime = repo.save(realTimeWeather);

        assertThat(updatedRealTime.getHumidity()).isEqualTo(52);

    }

    @Test
    public void testFindByCountryCodeAndCityNotFound(){
        String countryCode = "JP";
        String cityName = "Tokyo";

        RealTimeWeather realTimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName).orElse(null);
        assertThat(realTimeWeather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityFound(){
        String countryCode = "IN";
        String cityName = "Mumbai";

        RealTimeWeather realTimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName).orElse(null);

        assertThat(realTimeWeather).isNotNull();
        assertThat(realTimeWeather.getLocation().getCityName()).isEqualTo(cityName);
    }

    @Test
    public void testFindByLocationNotFound() {
        String locationCode = "ABC";
        RealTimeWeather realTimeWeather = repo.findByLocationCode(locationCode).orElse(null);

        assertThat(realTimeWeather).isNull();
    }

    @Test
    public void testFindByLocationFound() {
        String locationCode = "MUB";
        RealTimeWeather realTimeWeather = repo
                .findByLocationCode(locationCode)
                        .orElseThrow(() -> new LocationNotFoundException("No Location found with the given code:" + locationCode));
        System.out.println(realTimeWeather);

        assertThat(realTimeWeather).isNotNull();
    }

}
