package com.oak.application.api;


import com.oak.application.Handler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OakApplication extends Handler {
    public static void main(String[] args) {
        SpringApplication.run(OakApplication.class, args);
    }
}
