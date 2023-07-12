package com.WeatherAPI;

import com.WeatherAPI.dao.LocationRepo;
import com.WeatherAPI.entity.Location;
import com.WeatherAPI.entity.RealTimeWeather;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
public class LocationRepoTest {
    @Autowired
    private LocationRepo locationRepo;
    private static final String END_POINT_PATH = "/v1/location";

    @Test
    public void testAddSuccess(){
        Location location = new Location();
        location.setCode("NY_State");
        location.setCityName("New York State");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        location.setTrashed(true);

        Location savedLocation = locationRepo.save(location);

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getCode()).isEqualTo("NY_State");
    }

    @Test
    public void testListSuccess(){
        List<Location> untrashed = locationRepo.findUntrashed();

    //    assertThat(untrashed).isNotNull();
        untrashed.forEach(System.out::println);
    }

    @Test
    public void testGetNotFound(){
        String code = "ABCD";
        Location location = locationRepo.findByCode(code);

        assertThat(location).isNull();
    }

    @Test
    public void testGetFound(){
        String code = "NY_State";
        Location location = locationRepo.findByCode(code);

        assertThat(location).isNotNull();
        assertThat(location.getCode()).isEqualTo(code);
    }

    @Test
    public void testGetShouldReturn405MethodNotAllowed() {
      //  String requestURI = END_POINT_PATH + "/ABCDE";

    }
    @Test
    public void testGetShouldReturn404NotFound() {
        //  String requestURI = END_POINT_PATH + "/ABCDE";

    }

    @Test
    public void testTrashedSuccess(){
        String code = "NYC_USA";
        locationRepo.trashedByCode(code);

        Location location = locationRepo.findByCode(code);// this should be null after trashed

        assertThat(location).isNull();
    }

    @Test
    public void testAddRealTimeWeatherData() {
        String code = "BLR";

        Location location = locationRepo.findByCode(code);

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

        Location updatedLocation = locationRepo.save(location);

        assertThat(updatedLocation.getRealTimeWeather().getLocationCode()).isEqualTo(code);
    }
}
