package gtr3base.dev.hotel_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel API")
                        .version("1.0")
                        .description("RESTful API for hotel management")
                        .contact(new Contact()
                                .name("gtr3base")
                                .email("e92hell0rain@gmail.com")));
    }
}
