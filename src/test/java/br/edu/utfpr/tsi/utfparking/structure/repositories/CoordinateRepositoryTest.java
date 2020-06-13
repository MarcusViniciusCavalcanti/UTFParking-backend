package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CoordinateRepositoryTest {

    private static final float AXIOS_X = 10F;
    private static final float AXIOS_Y = 10F;

    @Autowired
    private CoordinateRepository coordinateRepository;

    private Long coordinateId;

    @Test
    void shouldSaveNewCoordinate() {
        var coordinate = Coordinate.builder()
                .axiosX(AXIOS_X)
                .axiosY(AXIOS_Y)
                .build();

        coordinateId = coordinateRepository.save(coordinate).getId();
        assertNotNull(coordinateId);
    }

    @Test
    void shouldReturnCoordinate() {
        var mock = Coordinate.builder()
                .axiosX(AXIOS_X + 1)
                .axiosY(AXIOS_Y + 1)
                .build();

        coordinateId = coordinateRepository.save(mock).getId();

        coordinateRepository.findById(coordinateId).ifPresent(coordinate -> {
            assertEquals(AXIOS_X + 1, coordinate.getAxiosX());
            assertEquals(AXIOS_Y + 1, coordinate.getAxiosY());
            assertNotNull(coordinate.getCreatedAt());
        });
    }
}
