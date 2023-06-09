package com.bobocode;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class CqrsNasaLargestMarsPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsNasaLargestMarsPictureApplication.class, args);
    }

}
