package br.edu.utfpr.tsi.utfparking.domain.security.entity;

import br.edu.utfpr.tsi.utfparking.structure.dtos.RoleDTO;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Builder
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    public RoleDTO createDto() {
        return RoleDTO.builder()
            .id(id)
            .description(description)
            .name(name)
            .build();
    }
}
