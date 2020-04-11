package br.edu.utfpr.tsi.utfparking.domain.users.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "configuration-development")
public class PropertiesDevelopment {

    private boolean isMock = false;

}
