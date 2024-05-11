package com.WeatherAPI.dao;

import com.WeatherAPI.entity.DailyWeather;
import com.WeatherAPI.entity.HourlyWeather;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import com.WeatherAPI.exception.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;
    private static final String END_POINT_PATH = "/v1/location";

    @Test
    public void testAddSuccess(){
        Location location = new Location();
        location.setCode("MUB");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);

        Location savedLocation = locationRepository.save(location);

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getCode()).isEqualTo(location.getCode());
    }

    @Test
    public void testListSuccess(){
        List<Location> untrashed = locationRepository.findUntrashed();

    //    assertThat(untrashed).isNotNull();
        untrashed.forEach(System.out::println);
    }

    @Test
    public void testGetNotFound(){
        String code = "ABCD";
        Location location = locationRepository
                .findByCode(code).orElse(null);

        assertThat(location).isNull();
    }

    @Test
    public void testGetFound(){
        String code = "MUB";
        Location location = locationRepository
                .findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        System.out.println("Printing Location Details " + location);
        assertThat(location).isNotNull();
        assertThat(location.getCode()).isEqualTo(code);
    }


    @Test
    public void testGetShouldReturn404NotFound() {
        //  String requestURI = END_POINT_PATH + "/ABCDE";

    }

    @Test
    public void testTrashedSuccess(){
        String code = "NYC_USA";
        locationRepository.trashedByCode(code);

        Location location = locationRepository.findByCode(code).orElse(null);// this should be null after trashed

        assertThat(location).isNull();
    }

    @Test
    public void testAddRealTimeWeatherData() {
        String code = "BLR";

        Location location = locationRepository.findByCode(code)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        RealTimeWeather realTimeWeather = location.getRealTimeWeather();

        if(realTimeWeather == null){
            realTimeWeather = new RealTimeWeather();

            realTimeWeather.setLocation(location);
            location.setRealTimeWeather(realTimeWeather);
        }
        realTimeWeather.setTemperature(25);
        realTimeWeather.setHumidity(60);
        realTimeWeather.setPrecipitation(90);
        realTimeWeather.setStatus("cloudy");
        realTimeWeather.setWindSpeed(40);
        realTimeWeather.setLastUpdated(new Date());

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getRealTimeWeather().getLocationCode()).isEqualTo(code);
    }

    @Test
    public void testAddHourlyData() {
        Location location = locationRepository.findByCode("MUB")
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        List<HourlyWeather> hourlyWeatherList = location.getHourlyWeatherList();

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(28)
                .precipitation(40)
                .status("cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(12)
                .temperature(30)
                .precipitation(45)
                .status("cloudy");

        hourlyWeatherList.add(forecast1);
        hourlyWeatherList.add(forecast2);

        // we are saving parent object, we have done casacde Type All in Location class
        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getHourlyWeatherList().size()).isEqualTo(2);
    }

    @Test
    public void testFindByCountryCodeAndCityNotFound() {
        String countryCode = "KLJ";
        String cityName = "city";

        Location location = locationRepository
                .findByCountryNameAndCityName(countryCode, cityName)
                        .orElse(null);

        assertThat(location).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityFound() {
        String countryCode = "IN";
        String cityName = "Mumbai";

        Location location = locationRepository
                .findByCountryNameAndCityName(countryCode, cityName)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given country code and city name"));

        assertThat(location).isNotNull();
        assertThat(location.getCountryCode()).isEqualTo(countryCode);
        assertThat(location.getCityName()).isEqualTo(cityName);
    }

    @Test
    public void testAddRealtimeWeatherData() {
        String locationCode = "MUB";

        Location location = locationRepository.findByCode(locationCode)
                .orElseThrow(() -> new LocationNotFoundException("No Location Found with the given code"));

        RealTimeWeather realTimeWeather = location.getRealTimeWeather();

        if(realTimeWeather == null) {
            realTimeWeather = new RealTimeWeather();
            realTimeWeather.setLocation(location);

            location.setRealTimeWeather(realTimeWeather);
        }

        realTimeWeather.setTemperature(10);
        realTimeWeather.setHumidity(60);
        realTimeWeather.setPrecipitation(70);
        realTimeWeather.setStatus("Cloudy");
        realTimeWeather.setWindSpeed(10);
        realTimeWeather.setLastUpdated(new Date());

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getRealTimeWeather().getLocationCode()).isEqualTo(locationCode);
    }

    @Test
    public void testAddHourlyWeatherData() {
        Location location = locationRepository
                .findById("MUB")
                .orElseThrow(() -> new LocationNotFoundException("No Location found with the given ID"));
        List<HourlyWeather> hourlyWeatherList = location.getHourlyWeatherList();

        HourlyWeather forecast1 =
                new HourlyWeather().id(location, 8) // This is Composite key having location and Hour of the Day
                        .temperature(20)
                        .precipitation(55)
                        .status("Nice");

        HourlyWeather forecast2 =
                new HourlyWeather().id(location, 9) // This is Composite key having location and Hour of the Day
                        .temperature(21)
                        .precipitation(50)
                        .status("Cool");

        hourlyWeatherList.add(forecast1);
        hourlyWeatherList.add(forecast2);

        // Cascade Type All in Location so the HourlyWeather Objects will also be Persisted
        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getHourlyWeatherList()).isNotNull();
    }

    @Test
    public void testAddDailyWeatherData() {
        Location location = locationRepository.findById("MUB").get();

        List<DailyWeather> dailyWeatherList = location.getDailyWeather();

        DailyWeather forcecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(5)
                .minTemp(25)
                .maxTemp(33)
                .precipitation(20)
                .status("Cloudy");

        DailyWeather forcecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(5)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(26)
                .status("Clear");

        dailyWeatherList.add(forcecast1);
        dailyWeatherList.add(forcecast2);

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getDailyWeather().size()).isGreaterThan(0);
    }
}
