package com.WeatherAPI.dao;

import com.WeatherAPI.dao.filter.FilterableLocationRepository;
import com.WeatherAPI.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, String>, FilterableLocationRepository {

    @Deprecated
    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    List<Location> findUntrashed();

    @Query("SELECT l FROM Location l WHERE l.trashed = false")
    Page<Location> findUntrashed(Pageable pageable);

    @Query("SELECT l FROM Location l WHERE l.trashed = false AND l.code = ?1")
    Optional<Location>  findByCode(String code);

    @Query("UPDATE Location SET trashed = true WHERE code = ?1")
    @Modifying
    void trashedByCode(String code);

    @Query("UPDATE Location SET trashed = false WHERE code = ?1")
    @Modifying
    void untrashedByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 AND l.trashed = false")
    Optional<Location> findByCountryNameAndCityName(String countryCode, String cityName);

}
