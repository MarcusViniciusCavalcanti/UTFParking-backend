package br.edu.utfpr.tsi.utfparking.domain.configuration.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "application_config")
@EqualsAndHashCode
@ToString
public class ApplicationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mode_system")
    private String modeSystem;

    @Column(name = "ip")
    private String ip;

    public ApplicationConfig(Integer id, String modeSystem) {
        this.id = id;
        this.modeSystem = modeSystem;
    }
}
