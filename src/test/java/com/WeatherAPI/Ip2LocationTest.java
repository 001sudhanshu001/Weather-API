package com.WeatherAPI;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class Ip2LocationTest {
    private String DBPath = "src/main/java/com/WeatherAPI/ip2LocationDB/IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testInvalidIP() throws IOException {
        IP2Location ip2Locator = new IP2Location();
        ip2Locator.Open(DBPath);

        String ipAddress = "abc";
        IPResult ipResult = ip2Locator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");

        System.out.println(ipResult);
    }

    @Test
    public void testValidIP1() throws IOException {
        IP2Location ip2Locator = new IP2Location();
        ip2Locator.Open(DBPath);

        String ipAddress = "108.30.178.78";
        IPResult ipResult = ip2Locator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("New York City");

        System.out.println(ipResult);
    }

    @Test
    public void testValidIP2() throws IOException {
        IP2Location ip2Locator = new IP2Location();
        ip2Locator.Open(DBPath);

        String ipAddress = "103.48.198.141";
        IPResult ipResult = ip2Locator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Delhi");

        System.out.println(ipResult);
    }


}
