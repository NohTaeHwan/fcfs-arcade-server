package com.josh.toy.fcfsarcade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FcfsArcadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcfsArcadeApplication.class, args);
    }

}
