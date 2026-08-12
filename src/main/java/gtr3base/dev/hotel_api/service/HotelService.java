package gtr3base.dev.hotel_api.service;

import gtr3base.dev.hotel_api.dto.request.HotelRequest;
import gtr3base.dev.hotel_api.dto.response.HotelBriefResponse;
import gtr3base.dev.hotel_api.dto.response.HotelDetailedResponse;

import java.util.List;
import java.util.Map;

public interface HotelService {
    List<HotelBriefResponse> getAllHotels();
    HotelDetailedResponse getHotelById(Long id);
    List<HotelBriefResponse> searchHotels(String name, String brand, String city, String country, List<String> amenities);
    HotelBriefResponse createHotel(HotelRequest hotelRequest);
    void addAmenities(Long hotelId, List<String> amenityNames);
    Map<String, Long> getHistogram(String param);
}
