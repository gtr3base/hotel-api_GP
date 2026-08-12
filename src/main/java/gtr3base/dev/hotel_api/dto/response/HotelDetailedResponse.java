package gtr3base.dev.hotel_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelDetailedResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private AddressResponse address;
    private ContactsResponse contacts;
    private ArrivalTimeResponse arrivalTime;
    private List<String> amenities;

    @Data
    @Builder
    public static class AddressResponse {
        private Integer houseNumber;
        private String street;
        private String city;
        private String country;
        private String postCode;
    }

    @Data
    @Builder
    public static class ContactsResponse {
        private String phone;
        private String email;
    }

    @Data
    @Builder
    public static class ArrivalTimeResponse {
        private String checkIn;
        private String checkOut;
    }
}
