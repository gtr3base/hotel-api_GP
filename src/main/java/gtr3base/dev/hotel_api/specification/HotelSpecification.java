package gtr3base.dev.hotel_api.specification;

import gtr3base.dev.hotel_api.entity.Hotel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HotelSpecification {

    public static Specification<Hotel> searchHotels(
            String name,
            String brand,
            String city,
            String country,
            List<String> amenities
    ){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(name != null && !name.isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if(brand != null && !brand.isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("brand")),
                        "%" + brand.toLowerCase() + "%"
                ));
            }

            if(city != null && !city.isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address").get("city")),
                        "%" + city.toLowerCase() + "%"
                ));
            }

            if(country != null && !country.isEmpty()){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address").get("country")),
                        "%" + country.toLowerCase() + "%"
                ));
            }

            if(amenities != null && !amenities.isEmpty()){
                Join<Object, Object> amenitiesJoin = root.join("amenities");
                predicates.add(amenitiesJoin.get("name").in(amenities));
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
