package com.project.casualtalkchat;

import com.project.casualtalkchat.common.ApplicationResourcesRepository;
import com.project.casualtalkchat.common.FileCouldNotBeSavedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ApplicationResourcesRepository resourcesRepository() {
        return new ApplicationResourcesRepository() {
            @Override
            public void saveFile(String path, byte[] file) throws FileCouldNotBeSavedException {
                ApplicationResourcesRepository.super.saveFile(path, file);
            }
        };
    }
}
