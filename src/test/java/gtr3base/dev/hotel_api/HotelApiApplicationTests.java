package gtr3base.dev.hotel_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gtr3base.dev.hotel_api.dto.request.HotelRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HotelApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static Long createdHotelId;

    @Test
	@Order(1)
	void testCreateHotel() throws Exception {
		HotelRequest request = createTestHotelRequest();

		String response = mockMvc.perform(post("/hotels")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
				.andExpect(jsonPath("$.address").exists())
				.andExpect(jsonPath("$.phone").value("+375 29 309 80 00"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode jn = objectMapper.readTree(response);
		createdHotelId = jn.get("id").asLong();
	}

	@Test
	@Order(2)
	void testGetAllHotels() throws Exception {
		mockMvc.perform(get("/hotels"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));
	}

	@Test
	@Order(3)
	void testGetHotelById() throws Exception {
		mockMvc.perform(get("/hotels/{id}", createdHotelId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(createdHotelId))
				.andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
				.andExpect(jsonPath("$.brand").value("Hilton"))
				.andExpect(jsonPath("$.address.city").value("Minsk"))
				.andExpect(jsonPath("$.address.country").value("Belarus"))
				.andExpect(jsonPath("$.contacts.phone").value("+375 29 309 80 00"))
				.andExpect(jsonPath("$.contacts.email").value("doubletreeminsk.info@hilton.com"))
				.andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
				.andExpect(jsonPath("$.amenities").isArray());
	}

	@Test
	@Order(4)
	void testSearchHotels() throws Exception {
		mockMvc.perform(get("/search")
						.param("city", "minsk"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton Minsk"));

		mockMvc.perform(get("/search")
						.param("brand", "Hilton"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));

		mockMvc.perform(get("/search")
						.param("country", "Belarus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	@Order(5)
	void testAddAmenities() throws Exception {
		List<String> amenities = Arrays.asList(
				"Free parking",
				"Free WiFi",
				"Non-smoking rooms",
				"Concierge",
				"On-site restaurant",
				"Fitness center",
				"Pet-friendly rooms",
				"Room service",
				"Business center",
				"Meeting rooms"
		);

		mockMvc.perform(post("/hotels/{id}/amenities", createdHotelId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(amenities)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/hotels/{id}", createdHotelId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amenities", hasSize(10)))
				.andExpect(jsonPath("$.amenities[0]").value("Business center"));
	}

	@Test
	@Order(6)
	void testHistogram() throws Exception {
		mockMvc.perform(get("/histogram/city"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.Minsk").value(1));

		mockMvc.perform(get("/histogram/country"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.Belarus").value(1));

		mockMvc.perform(get("/histogram/brand"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.Hilton").value(1));

		mockMvc.perform(get("/histogram/amenities"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.['Free parking']").value(1))
				.andExpect(jsonPath("$.['Free WiFi']").value(1));
	}

	@Test
	@Order(7)
	void testInvalidHistogramParam() throws Exception {
		mockMvc.perform(get("/histogram/invalid"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@Order(8)
	void testHotelNotFound() throws Exception {
		mockMvc.perform(get("/hotels/{id}", 9999L))
				.andExpect(status().isNotFound());
	}

	private HotelRequest createTestHotelRequest() {
		HotelRequest request = new HotelRequest();
		request.setName("DoubleTree by Hilton Minsk");
		request.setDescription("The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital");
		request.setBrand("Hilton");

		HotelRequest.AddressRequest address = new HotelRequest.AddressRequest();
		address.setHouseNumber(9);
		address.setStreet("Pobediteley Avenue");
		address.setCity("Minsk");
		address.setCountry("Belarus");
		address.setPostCode("220004");
		request.setAddress(address);

		HotelRequest.ContactsRequest contacts = new HotelRequest.ContactsRequest();
		contacts.setPhone("+375 29 309 80 00");
		contacts.setEmail("doubletreeminsk.info@hilton.com");
		request.setContacts(contacts);

		HotelRequest.ArrivalTimeRequest arrivalTime = new HotelRequest.ArrivalTimeRequest();
		arrivalTime.setCheckIn("14:00");
		arrivalTime.setCheckOut("12:00");
		request.setArrivalTime(arrivalTime);

		return request;
	}
}
