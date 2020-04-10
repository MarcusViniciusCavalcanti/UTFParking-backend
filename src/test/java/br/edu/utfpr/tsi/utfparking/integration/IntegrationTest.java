package br.edu.utfpr.tsi.utfparking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public abstract class IntegrationTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();


    protected MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    protected ObjectMapper mapper = new ObjectMapper();

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    protected <T> List<T> mapListFromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
}
