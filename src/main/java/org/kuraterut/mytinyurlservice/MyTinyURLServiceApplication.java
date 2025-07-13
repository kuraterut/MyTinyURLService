package org.kuraterut.mytinyurlservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyTinyURLServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyTinyURLServiceApplication.class, args);
    }
}