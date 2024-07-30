package com.WeatherAPI.criteria;

import com.WeatherAPI.entity.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationCriteriaQueryTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCriteriaQuery() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> query = criteriaBuilder.createQuery(Location.class);

        Root<Location> root = query.from(Location.class);

        // WHERE Clause
        Predicate predicate = criteriaBuilder.equal(root.get("countryName"), "India");
        query.where(predicate);

        // ORDER BY
        query.orderBy(criteriaBuilder.asc(root.get("cityName")));

        TypedQuery<Location> typedQuery = entityManager.createQuery(query);

        // Pagination
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(3);

        List<Location> resultList = typedQuery.getResultList();

        assertThat(resultList).isNotEmpty();

        resultList.forEach(System.out::println);
    }

    @Test
    public void testJPQL() {
        String jpql = "FROM Location";

        TypedQuery<Location> typedQuery = entityManager.createQuery(jpql, Location.class);
        List<Location> resultList = typedQuery.getResultList();

        assertThat(resultList).isNotEmpty();

        resultList.forEach(System.out::println);
    }
}
