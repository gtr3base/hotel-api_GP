package gtr3base.dev.hotel_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Contacts {

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email")
    private String email;
}
