package com.WeatherAPI.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable // This class acts as the Composite Primary Key
public class HourlyWeatherId implements Serializable {
    private int hourOfDay;
    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;
}
