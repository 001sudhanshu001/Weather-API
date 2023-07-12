package com.WeatherAPI;

import com.WeatherAPI.dao.RealTimeWeatherRepo;
import com.WeatherAPI.entity.RealTimeWeather;
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
        String code = "BLR";
        RealTimeWeather realTimeWeather = repo.findById(code).get();

        realTimeWeather.setTemperature(30);
        realTimeWeather.setHumidity(65);
        realTimeWeather.setPrecipitation(95);
        realTimeWeather.setStatus("cloudy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());

        RealTimeWeather updatedRealTime = repo.save(realTimeWeather);

        assertThat(updatedRealTime.getHumidity()).isEqualTo(65);

    }

    @Test
    public void testFindByCountryCodeAndCityNotFound(){
        String countryCode = "JP";
        String cityName = "Tokyo";

        RealTimeWeather realTimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);
        assertThat(realTimeWeather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityFound(){
        String countryCode = "IN";
        String cityName = "Banglore";

        RealTimeWeather realTimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);
        System.out.println(realTimeWeather);
        assertThat(realTimeWeather).isNotNull();
        assertThat(realTimeWeather.getLocation().getCityName()).isEqualTo(cityName);
    }

    @Test
    public void testFindByLocationNotFound() {
        String locationCode = "ABC";
        RealTimeWeather realTimeWeather = repo.findByLocationCode(locationCode);

        assertThat(realTimeWeather).isNull();
    }

    @Test
    public void testFindByLocationFound() {
        String locationCode = "BLR";
        RealTimeWeather realTimeWeather = repo.findByLocationCode(locationCode);
        System.out.println(realTimeWeather);

        assertThat(realTimeWeather).isNotNull();
    }

}
