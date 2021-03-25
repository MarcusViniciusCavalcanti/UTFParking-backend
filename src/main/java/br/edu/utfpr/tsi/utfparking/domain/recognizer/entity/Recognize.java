package br.edu.utfpr.tsi.utfparking.domain.recognizer.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "recognizers")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Builder
public class Recognize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid_daemon")
    private UUID uuid;

    @Column(name = "camera_id")
    private Integer cameraId;

    @Column(name = "origin")
    private String origin;

    @Column(name = "epoch_time")
    private LocalDateTime epochTime;

    @Column(name = "processing_time_ms")
    private Float processingTimeMs;

    @Column(name = "plate", nullable = false, length = 10)
    private String plate;

    @Column(name = "matches_template")
    private Integer matchesTemplate;

    @Column(name = "confidence")
    private Float confidence;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recognizes_has_coordinates",
            joinColumns = { @JoinColumn(name = "recognize_id") },
            inverseJoinColumns = { @JoinColumn(name = "coordinate_id") }
    )
    private List<Coordinate> coordinates;
    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    private void newRecognize() {
        this.createdAt = LocalDate.now();
    }

}
