package br.edu.utfpr.tsi.utfparking.domain.recognizer.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "coordinates")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@Builder
public class Coordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "axios_x")
    private Float axiosX;

    @Column(name = "axios_y")
    private Float axiosY;


    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    private void newCoordinate() {
        this.createdAt = LocalDate.now();
    }

}
