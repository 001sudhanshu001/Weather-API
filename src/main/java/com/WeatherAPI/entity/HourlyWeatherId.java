package com.WeatherAPI.entity;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable // This class acts as the Composite Primary Key
public class HourlyWeatherId implements Serializable {

    private int hourOfDay;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    public HourlyWeatherId() { }

    public HourlyWeatherId(int hourOfDay, Location location) {
        super();
        this.hourOfDay = hourOfDay;
        this.location = location;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hourOfDay, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HourlyWeatherId other = (HourlyWeatherId) obj;
        return hourOfDay == other.hourOfDay && Objects.equals(location, other.location);
    }
}
