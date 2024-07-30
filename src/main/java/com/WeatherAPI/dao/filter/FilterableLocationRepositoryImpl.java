package com.WeatherAPI.dao.filter;

import com.WeatherAPI.entity.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FilterableLocationRepositoryImpl implements FilterableLocationRepository{

    private final EntityManager entityManager;

    @Override
    public Page<Location> listWithFilter(Pageable pageable,
                                         Map<String, Object> filterFields) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> query = criteriaBuilder.createQuery(Location.class);

        Root<Location> root = query.from(Location.class);

        if(!filterFields.isEmpty()) {
            Predicate[] predicates = new Predicate[filterFields.size()];

            int i = 0;
            for (String fieldName : filterFields.keySet()) {
                Object fieldValue = filterFields.get(fieldName);
                predicates[i] = criteriaBuilder.equal(root.get(fieldName), fieldValue);
            }

            query.where(predicates);

        }

        List<Order> listOrder = new ArrayList<>();
        pageable.getSort().stream().forEach(order -> {
            listOrder.add(criteriaBuilder.asc(root.get(order.getProperty())));
        });

        query.orderBy(listOrder);

        TypedQuery<Location> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Location> listResult = typedQuery.getResultList();
        int totalRows = 0;
        return new PageImpl<>(listResult, pageable, totalRows);
    }
}
