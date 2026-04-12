package com.parksense;

import com.parksense.config.ParkingProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(ParkingProviderProperties.class)
public class ParkSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkSenseApplication.class, args);
    }
}
