package gtr3base.dev.hotel_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "hotels")
@ToString(exclude = "hotels")
@Entity
@Table(
        name = "amenities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_amenities_name",
                        columnNames = "name"
                )
        })
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Hotel> hotels = new HashSet<>();

    public Amenity(String name) {
        this.name = name;
        this.hotels = new HashSet<>();
    }
}
