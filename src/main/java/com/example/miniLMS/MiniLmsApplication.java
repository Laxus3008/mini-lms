package com.example.miniLMS;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Mini LMS API",
        version = "1.0",
        description = "API for managing courses, modules, lessons, and tracking user progress"
    )
)
public class MiniLmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiniLmsApplication.class, args);
    }
}
