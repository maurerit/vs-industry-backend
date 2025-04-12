package io.github.vaporsea.vsindustry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VsIndustryApplication {

    public static void main(String[] args) {
        SpringApplication.run(VsIndustryApplication.class, args);
    }

}
