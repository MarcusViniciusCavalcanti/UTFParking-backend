package br.edu.utfpr.tsi.utfparking.domain.security.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AccessCard.class)
public class AccessCard_ {
    /*
    @Column(name = "username", length = 200, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @Column(name = "enabled")
    private boolean enabled;
     */
    public static volatile SingularAttribute<AccessCard, Long> id;
    public static volatile SingularAttribute<AccessCard, String> username;
    public static volatile SingularAttribute<AccessCard, String> password;
    public static volatile SingularAttribute<AccessCard, Boolean> enabled;
    public static volatile SingularAttribute<AccessCard, Boolean> accountNonExpired;
    public static volatile SingularAttribute<AccessCard, Boolean> accountNonLocked;
    public static volatile SingularAttribute<AccessCard, Boolean> credentialsNonExpired;
    public static volatile ListAttribute<AccessCard, Role> roles;
}
