package com.WeatherAPI.criteria;

import com.WeatherAPI.dao.LocationRepository;
import com.WeatherAPI.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FilterableLocationRepositoryImplTests {

    @Autowired
    private LocationRepository repository;

    @Test
    public void testListWithDefaults() {
        int pageSize = 2;
        int pageNum = 0;
        String sortField = "code";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Location> page = repository.listWithFilter(pageable, Collections.emptyMap());

        List<Location> content = page.getContent();

        assertThat(content).size().isEqualTo(pageSize);

        content.forEach(System.out::println);
    }

    @Test
    public void testListNoFilterSortedByCityName() {
        int pageSize = 3;
        int pageNum = 0;
        String sortField = "cityName";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Location> page = repository.listWithFilter(pageable, Collections.emptyMap());

        List<Location> content = page.getContent();

        assertThat(content).size().isEqualTo(pageSize);

        assertThat(content).isSortedAccordingTo(Comparator.comparing(Location::getCityName));

        content.forEach(System.out::println);
    }
}
