package gtr3base.dev.hotel_api;

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
import gtr3base.dev.hotel_api.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel testHotel;
    private HotelRequest testRequest;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setDescription("Test Description");
        testHotel.setBrand("Test Brand");

        Address address = new Address(1, "Test Street", "Minsk", "Belarus", "220000");
        testHotel.setAddress(address);

        Contacts contacts = new Contacts("+375 29 309 80 007", "test@hotel.com");
        testHotel.setContacts(contacts);

        ArrivalTime arrivalTime = new ArrivalTime(
                LocalTime.parse("14:00", formatter), LocalTime.parse("12:00", formatter)
        );
        testHotel.setArrivalTime(arrivalTime);

        testHotel.setAmenities(new HashSet<>());

        testRequest = new HotelRequest();
        testRequest.setName("Test Hotel");
        testRequest.setDescription("Test Description");
        testRequest.setBrand("Test Brand");

        HotelRequest.AddressRequest addressRequest = new HotelRequest.AddressRequest();
        addressRequest.setHouseNumber(1);
        addressRequest.setStreet("Test Street");
        addressRequest.setCity("Minsk");
        addressRequest.setCountry("Belarus");
        addressRequest.setPostCode("220000");
        testRequest.setAddress(addressRequest);

        HotelRequest.ContactsRequest contactsRequest = new HotelRequest.ContactsRequest();
        contactsRequest.setPhone("+375 29 309 80 007");
        contactsRequest.setEmail("test@hotel.com");
        testRequest.setContacts(contactsRequest);
    }

    @Test
    void getAllHotels_ShouldReturnList() {
        when(hotelRepository.findAll()).thenReturn(Collections.singletonList(testHotel));

        List<HotelBriefResponse> hotels = hotelService.getAllHotels();

        assertNotNull(hotels);
        assertEquals(1, hotels.size());
        assertEquals("Test Hotel", hotels.getFirst().getName());
    }

    @Test
    void getHotelById_ShouldReturnHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        HotelDetailedResponse response = hotelService.getHotelById(1L);

        assertNotNull(response);
        assertEquals("Test Hotel", response.getName());
        assertEquals("Minsk", response.getAddress().getCity());
    }

    @Test
    void getHotelById_ShouldThrowException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> {
            hotelService.getHotelById(99L);
        });
    }

    @Test
    void createHotel_ShouldSaveAndReturn() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        HotelBriefResponse response = hotelService.createHotel(testRequest);

        assertNotNull(response);
        assertEquals("Test Hotel", response.getName());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void addAmenities_ShouldAddNewAmenities() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(amenityRepository.findByName("Free WiFi")).thenReturn(Optional.empty());
        when(amenityRepository.save(any(Amenity.class))).thenAnswer(invocation -> {
            Amenity amenity = invocation.getArgument(0);
            amenity.setId(1L);
            return amenity;
        });
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        hotelService.addAmenities(1L, List.of("Free WiFi"));

        verify(amenityRepository, times(1)).save(any(Amenity.class));
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void addAmenities_ShouldUseExistingAmenities() {
        Amenity existingAmenity = new Amenity();
        existingAmenity.setId(1L);
        existingAmenity.setName("Free WiFi");
        existingAmenity.setHotels(new HashSet<>());

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(amenityRepository.findByName("Free WiFi")).thenReturn(Optional.of(existingAmenity));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        hotelService.addAmenities(1L, List.of("Free WiFi"));

        verify(amenityRepository, never()).save(any(Amenity.class));
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void addAmenities_ShouldThrowHotelNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> {
            hotelService.addAmenities(99L, List.of("Free WiFi"));
        });
    }
}