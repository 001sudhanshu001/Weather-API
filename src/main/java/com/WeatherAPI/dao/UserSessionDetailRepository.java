package com.WeatherAPI.dao;

import com.WeatherAPI.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionDetailRepository extends JpaRepository<UserSession, Long> {
}
