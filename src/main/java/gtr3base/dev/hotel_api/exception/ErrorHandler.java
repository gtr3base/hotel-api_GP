package gtr3base.dev.hotel_api.exception;

public class ErrorHandler {
    public static final String HOTEL_NOT_FOUND = "Hotel not found with id: %s";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error: %s";
    public static final String INVALID_HISTOGRAM_PARAM = "Invalid histogram parameter: %s. Valid parameters are: brand, city, country, amenities";
}
