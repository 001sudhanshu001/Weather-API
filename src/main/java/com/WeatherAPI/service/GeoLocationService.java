package com.WeatherAPI.service;

import com.WeatherAPI.entity.Location;
import com.WeatherAPI.exception.GeoLocationException;
import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeoLocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoLocationService.class);
    private static final String DBPath = "src/main/java/com/WeatherAPI/ip2LocationDB/IP2LOCATION-LITE-DB3.BIN";
//    private static final String DBPath = "src/main/resources/ip2LocationDB/IP2LOCATION-LITE-DB3.BIN";
//    private static final String DBPath = "/ip2LocationDB/IP2LOCATION-LITE-DB3.BIN";
    private static final IP2Location ipLocator = new IP2Location();

    public GeoLocationService(){
        try {
            ipLocator.Open(DBPath);
        }catch (IOException ex){
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Cacheable(value = "locationByIp", key = "#ipAddress")
    public Location getLocationFromIpAddress(String ipAddress) throws GeoLocationException {
        try {
            IPResult result = ipLocator.IPQuery(ipAddress);
            
          //  LOGGER.info(result.toString());

            if(!result.getStatus().equals("OK")){
                throw new GeoLocationException("GeoLocation failed with status " + result.getStatus());
            }
            return new Location(result.getCity(), result.getRegion(), result.getCountryLong(),result.getCountryShort());
        } catch (IOException ex) {
            throw new  GeoLocationException("Error querying IP database", ex);
        }
    }
}
