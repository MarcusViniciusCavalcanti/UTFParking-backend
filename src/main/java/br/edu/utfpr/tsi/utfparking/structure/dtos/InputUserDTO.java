package br.edu.utfpr.tsi.utfparking.structure.dtos;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class InputUserDTO {

    private Long id;

    @NotBlank
    @Size(min = 5, max = 255)
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    @Size(min = 5, max = 200)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;

    @NotEmpty
    private List<Long> authorities;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private String carPlate;

    private String carModel;

    public InputUserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getAccessCard().getUsername();

        this.accountNonExpired = user.getAccessCard().isAccountNonExpired();
        this.accountNonLocked = user.getAccessCard().isAccountNonLocked();
        this.credentialsNonExpired = user.getAccessCard().isCredentialsNonExpired();
        this.enabled = user.getAccessCard().isEnabled();
        this.authorities = user.getAccessCard().getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }
}
