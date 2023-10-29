package com.WeatherAPI.dao;

import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.HourlyWeatherId;
import com.WeatherAPI.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class HourlyWeatherRepositoryTest {
    @Autowired
    private HourlyWeatherRepository hourlyRepo;

    @Test
    public void testAdd() {
        String locationCode = "MUB";
        Location location = new Location().code(locationCode); // we will get just location object location From DB

        int hourOfDay = 11;
        HourlyWeather forecast = new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(23)
                .precipitation(60)
                .status("Thunder Storm");

        HourlyWeather updatedForcast = hourlyRepo.save(forecast);

        assertThat(updatedForcast.getId().getLocation().getCode()).isEqualTo(locationCode);
        assertThat(updatedForcast.getId().getHourOfDay()).isEqualTo(hourOfDay);
    }

    @Test
    public void testDelete() {
        Location location = new Location().code("MUB");

        HourlyWeatherId id = new HourlyWeatherId(10, location);

        hourlyRepo.deleteById(id);

        Optional<HourlyWeather> result = hourlyRepo.findById(id);

        assertThat(result).isNotPresent();

    }

    @Test
    public void testFindByLocationCodeFound() {
        String locationCode = "MUB";
        int currentHour = 7;

        List<HourlyWeather> hourlyWeatherList = hourlyRepo.findByLocationCode(locationCode, currentHour);

        System.out.println("Printing the List :");
        for(var data : hourlyWeatherList){
            System.out.println(data);
        }
        assertThat(hourlyWeatherList.size()).isGreaterThan(0);
    }

    @Test
    public void testFindByLocationCodeNotFound() {
        String locationCode = "DELHI";
        int currentHour = 15;

        List<HourlyWeather> hourlyWeatherList = hourlyRepo.findByLocationCode(locationCode, currentHour);

        assertThat(hourlyWeatherList.size()).isEqualTo(0);
    }
}