package br.edu.utfpr.tsi.utfparking.structure.disk.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "files")
public class DiskProperties {

    private String path = "avatar";

}
