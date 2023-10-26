package com.project.casualtalkchat;

import com.project.casualtalkchat.common.UserImagesRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserImagesRepository resourcesRepository() {
        return new UserImagesRepository() {

        };
    }
}
