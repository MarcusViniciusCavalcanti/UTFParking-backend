package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Recognize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RecognizeRepository extends JpaRepository<Recognize, Long> {
    Optional<Recognize> findByPlateAndEpochTimeBetween(String plate, LocalDateTime start, LocalDateTime end);
}
