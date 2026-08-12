package gtr3base.dev.hotel_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequest {

    @NotBlank(message = "Hotel name is required")
    private String name;

    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Address is required")
    @Valid
    private AddressRequest address;

    @NotNull(message = "Contacts are required")
    @Valid
    private ContactsRequest contacts;

    @Valid
    private ArrivalTimeRequest arrivalTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArrivalTimeRequest {
        @NotBlank(message = "Check-in time is required")
        private String checkIn;
        private String checkOut;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressRequest {
        @NotNull(message = "House number is required")
        private Integer houseNumber;

        @NotBlank(message = "Street is required")
        private String street;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "Country is required")
        private String country;

        @NotBlank(message = "Post code is required")
        private String postCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactsRequest {

        @NotBlank(message = "Phone is required")
        private String phone;

        private String email;
    }
}
