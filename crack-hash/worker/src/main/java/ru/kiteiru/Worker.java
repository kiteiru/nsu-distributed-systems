package ru.kiteiru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Worker {
    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }
}