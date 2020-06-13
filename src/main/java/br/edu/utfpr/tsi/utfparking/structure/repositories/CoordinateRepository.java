package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.recognizer.entity.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinateRepository extends JpaRepository<Coordinate, Long> {
}
