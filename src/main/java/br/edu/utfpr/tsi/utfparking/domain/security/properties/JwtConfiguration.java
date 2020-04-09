package br.edu.utfpr.tsi.utfparking.domain.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt.config")
@Getter
@Setter
public class JwtConfiguration {

    private String loginUrl = "/api/v1/login/**";

    private Header header = new Header();

    private int expiration = 3600;

    private String secretKey = "qxBEEQv7E8aviX1KUcdOiF5ve5COUPAr";

    private String type = "encrypted";

    public Date expirationTimeDate() {
        return new Date(System.currentTimeMillis() + getExpiration() * 1000);
    }

    @Getter
    @Setter
    public static class Header {

        private String name = "Authorization";

        private String prefix = "Bearer ";
    }
}
