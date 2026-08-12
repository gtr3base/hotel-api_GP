package gtr3base.dev.hotel_api.service.impl;

import gtr3base.dev.hotel_api.dto.request.HotelRequest;
import gtr3base.dev.hotel_api.dto.response.HotelBriefResponse;
import gtr3base.dev.hotel_api.dto.response.HotelDetailedResponse;
import gtr3base.dev.hotel_api.entity.Address;
import gtr3base.dev.hotel_api.entity.Amenity;
import gtr3base.dev.hotel_api.entity.ArrivalTime;
import gtr3base.dev.hotel_api.entity.Contacts;
import gtr3base.dev.hotel_api.entity.Hotel;
import gtr3base.dev.hotel_api.exception.HotelNotFoundException;
import gtr3base.dev.hotel_api.repository.AmenityRepository;
import gtr3base.dev.hotel_api.repository.HotelRepository;
import gtr3base.dev.hotel_api.service.HotelService;
import gtr3base.dev.hotel_api.specification.HotelSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gtr3base.dev.hotel_api.exception.ErrorHandler.HOTEL_NOT_FOUND;
import static gtr3base.dev.hotel_api.exception.ErrorHandler.INVALID_HISTOGRAM_PARAM;

@Service
@Transactional
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public List<HotelBriefResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::mapToBriefResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailedResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(String.format(HOTEL_NOT_FOUND, id)));
        return mapToDetailedResponse(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelBriefResponse> searchHotels(String name, String brand, String city, String country, List<String> amenities) {
        Specification<Hotel> specification = HotelSpecification.searchHotels(name, brand, city, country, amenities);

        return hotelRepository.findAll(specification).stream()
                .map(this::mapToBriefResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HotelBriefResponse createHotel(HotelRequest hotelRequest) {
        Hotel hotel = Hotel.builder()
                .name(hotelRequest.getName())
                .description(hotelRequest.getDescription())
                .brand(hotelRequest.getBrand())
                .build();

        if(hotelRequest.getAddress() != null) {
            Address address = Address.builder()
                    .houseNumber(hotelRequest.getAddress().getHouseNumber())
                    .street(hotelRequest.getAddress().getStreet())
                    .city(hotelRequest.getAddress().getCity())
                    .country(hotelRequest.getAddress().getCountry())
                    .postCode(hotelRequest.getAddress().getPostCode())
                    .build();
            hotel.setAddress(address);
        }

        if(hotelRequest.getContacts() != null){
            Contacts contacts = Contacts.builder()
                    .phone(hotelRequest.getContacts().getPhone())
                    .email(hotelRequest.getContacts().getEmail())
                    .build();
            hotel.setContacts(contacts);
        }

        if(hotelRequest.getArrivalTime() != null){
            ArrivalTime arrivalTime = new ArrivalTime();
            if(hotelRequest.getArrivalTime().getCheckIn() != null){
                arrivalTime.setCheckIn(LocalTime.parse(hotelRequest.getArrivalTime().getCheckIn()));
            }
            if(hotelRequest.getArrivalTime().getCheckOut() != null){
                arrivalTime.setCheckOut(LocalTime.parse(hotelRequest.getArrivalTime().getCheckOut()));
            }
            hotel.setArrivalTime(arrivalTime);
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToBriefResponse(savedHotel);
    }

    @Override
    public void addAmenities(Long hotelId, List<String> amenityNames) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(String.format(HOTEL_NOT_FOUND, hotelId)));

        for(String aName : amenityNames){
            Amenity amenity = amenityRepository.findByName(aName)
                    .orElseGet(() -> amenityRepository.save(new Amenity(aName)));
            hotel.addAmenity(amenity);
        }

        hotelRepository.save(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getHistogram(String param) {
        switch (param.toLowerCase()) {
            case "brand":
                return createHistogram("brand");
            case "city":
                return createHistogram("address.city");
            case "country":
                return createHistogram("address.country");
            case "amenities":
                return createAmenitiesHistogram();
            default:
                throw new IllegalArgumentException(String.format(INVALID_HISTOGRAM_PARAM, param));
        }
    }

    private Map<String, Long> createAmenitiesHistogram() {
        String jpql = "SELECT a.name, COUNT(h) FROM Hotel h JOIN h.amenities a GROUP BY a.name ORDER BY COUNT(h) DESC";
        List<Object[]> results = em.createQuery(jpql, Object[].class).getResultList();

        Map<String, Long> histogram = new LinkedHashMap<>();
        for(Object[] result : results){
            String amenityName = (String) result[0];
            Long count = (Long) result[1];
            histogram.put(amenityName, count);
        }
        return histogram;
    }

    private Map<String, Long> createHistogram(String field) {
        String jpql = "SELECT h." + field + ", COUNT(h) FROM Hotel h GROUP BY h." + field;
        List<Object[]> results = em.createQuery(jpql, Object[].class).getResultList();

        Map<String, Long> histogram = new LinkedHashMap<>();
        for(Object[] result : results){
            String key = result[0] != null ? result[0].toString() : "null";
            Long value = (Long) result[1];
            histogram.put(key, value);
        }
        return histogram;
    }

    private HotelBriefResponse mapToBriefResponse(Hotel hotel) {
        String fullAddress = buildFullAddress(hotel.getAddress());

        return HotelBriefResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(fullAddress)
                .phone(hotel.getContacts() != null ? hotel.getContacts().getPhone() : null)
                .build();
    }

    private HotelDetailedResponse mapToDetailedResponse(Hotel hotel) {
        HotelDetailedResponse.AddressResponse addressResponse = null;
        if(hotel.getAddress() != null){
            Address addr = hotel.getAddress();
            addressResponse = HotelDetailedResponse.AddressResponse.builder()
                    .houseNumber(addr.getHouseNumber())
                    .street(addr.getStreet())
                    .city(addr.getCity())
                    .country(addr.getCountry())
                    .postCode(addr.getPostCode())
                    .build();
        }

        HotelDetailedResponse.ContactsResponse contactsResponse = null;
        if(hotel.getContacts() != null){
            Contacts con = hotel.getContacts();
            contactsResponse = HotelDetailedResponse.ContactsResponse.builder()
                    .phone(con.getPhone())
                    .email(con.getEmail())
                    .build();
        }

        HotelDetailedResponse.ArrivalTimeResponse arrivalTimeResponse = null;
        if(hotel.getArrivalTime() != null){
            ArrivalTime arrival = hotel.getArrivalTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            arrivalTimeResponse = HotelDetailedResponse.ArrivalTimeResponse.builder()
                    .checkIn(arrival.getCheckIn() != null ? arrival.getCheckIn().format(formatter) : null)
                    .checkOut(arrival.getCheckOut() != null ? arrival.getCheckOut().format(formatter) : null)
                    .build();
        }

        List<String> amenityList  = hotel.getAmenities().stream()
                .map(Amenity::getName)
                .sorted()
                .toList();

        return HotelDetailedResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .brand(hotel.getBrand())
                .address(addressResponse)
                .contacts(contactsResponse)
                .arrivalTime(arrivalTimeResponse)
                .amenities(amenityList)
                .build();
    }

    private String buildFullAddress(Address address){
        if(address == null) return null;
        return String.format("%d %s, %s, %s, %s",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry());
    }
}
