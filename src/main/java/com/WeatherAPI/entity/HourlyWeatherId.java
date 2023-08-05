package com.WeatherAPI.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter @Setter
@Embeddable
public class HourlyWeatherId implements Serializable {
    private int hourOfDay;
    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;
}
