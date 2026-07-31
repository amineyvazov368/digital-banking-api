package org.example.bankingsystemapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BankingSystemApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingSystemApiApplication.class, args);
    }

}
