package gtr3base.dev.hotel_api.controller;

import gtr3base.dev.hotel_api.dto.request.HotelRequest;
import gtr3base.dev.hotel_api.dto.response.HotelBriefResponse;
import gtr3base.dev.hotel_api.dto.response.HotelDetailedResponse;
import gtr3base.dev.hotel_api.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Hotel Controller", description = "API for hotel management")
public class HotelController {
    
    private final HotelService hotelService;

    @GetMapping("/hotels")
    @Operation(summary = "Get all hotels", description = "Returns list of all hotels with brief info")
    public ResponseEntity<List<HotelBriefResponse>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get hotel by ID", description = "Returns detailed info about specific hotel")
    public ResponseEntity<HotelDetailedResponse> getHotelById(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels", description = "Search hotels by name, brand, city, country, amenities")
    public ResponseEntity<List<HotelBriefResponse>> searchHotels(
            @Parameter(description = "Hotel name")
            @RequestParam(required = false) String name,
            @Parameter(description = "Hotel brand")
            @RequestParam(required = false) String brand,
            @Parameter(description = "City")
            @RequestParam(required = false) String city,
            @Parameter(description = "Country")
            @RequestParam(required = false) String country,
            @Parameter(description = "Amenities list")
            @RequestParam(required = false) List<String> amenities
    ){
        return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, country, amenities));
    }

    @PostMapping("/hotels")
    @Operation(summary = "Create hotel", description = "Creates a new hotel")
    public ResponseEntity<HotelBriefResponse> createHotel(
            @Valid @RequestBody HotelRequest hotelRequest
            ){
        HotelBriefResponse createdHotel = hotelService.createHotel(hotelRequest);
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @PostMapping("/hotels/{id}/amenities")
    @Operation(summary = "Add amenities", description = "Adds amenities to specific hotel")
    public ResponseEntity<Void> addAmenities(
            @Parameter(description = "Hotel ID", required = true)
            @PathVariable Long id,
            @RequestBody List<String> amenities
    ){
        hotelService.addAmenities(id, amenities);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/histogram/{param}")
    @Operation(summary = "Get histogram", description = "Returns histogram grouped by parameter(brand, city, country, amenities)")
    public ResponseEntity<Map<String, Long>> getHistogram(
            @Parameter(description = "Parameter for grouping", required = true)
            @PathVariable String param
    ){
        return ResponseEntity.ok(hotelService.getHistogram(param));
    }
}
