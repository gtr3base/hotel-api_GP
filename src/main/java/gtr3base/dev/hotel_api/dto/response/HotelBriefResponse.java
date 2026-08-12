package gtr3base.dev.hotel_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelBriefResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
}
