package br.edu.utfpr.tsi.utfparking.domain.users.entity;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

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
    @Enumerated(EnumType.STRING)
    private TypeUser typeUser;

    @Column(name = "number_access")
    private Integer numberAccess;

    @Column(name = "authorised_access")
    private Boolean authorisedAccess = Boolean.TRUE;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Car car;

    @PrePersist
    private void newAccessCard() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    private void updateAccessCard() {
        updatedAt = LocalDate.now();
    }

    public Optional<Car> car() {
        return Optional.ofNullable(car);
    }
}
