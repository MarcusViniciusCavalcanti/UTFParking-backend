package br.edu.utfpr.tsi.utfparking.domain.users.entity;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    @MapsId
    @JoinColumn(name = "access_card_id")
    private AccessCard accessCard;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private TypeUser typeUser;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Car car;

    @PrePersist
    private void newAccessCard() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    private void updateAccessCard() {
        this.updatedAt = LocalDate.now();
    }

    public Optional<Car> car() {
        return Optional.ofNullable(this.car);
    }
}
