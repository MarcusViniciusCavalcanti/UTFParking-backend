package br.edu.utfpr.tsi.utfparking.structure.disk.config;

import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebResourceConfiguration implements WebMvcConfigurer {

    private final DiskProperties diskProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations(String.format("file://%s", diskProperties.getPath()));
    }
}
