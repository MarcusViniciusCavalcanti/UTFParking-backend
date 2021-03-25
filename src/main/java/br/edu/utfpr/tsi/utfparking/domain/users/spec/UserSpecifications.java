package br.edu.utfpr.tsi.utfparking.domain.users.spec;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard_;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role_;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpecifications {
    public static final String QUALIFIER_GET_ALL_SPEC = "QUALIFIER_GET_ALL_SPEC";
    private static final String WILD_CARD = "%";

    public static Specification<User> nameOrUsernameContains(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(User_.name)),
                    criteriaBuilder.lower(criteriaBuilder.literal(concatenateKeyValueWithWildCard(value)))
                ),
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(User_.accessCard).get(AccessCard_.username)),
                    criteriaBuilder.lower(criteriaBuilder.literal(concatenateKeyValueWithWildCard(value)))
                )
            );
    }

    public static Specification<User> enabled(Boolean value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(User_.accessCard).get(AccessCard_.enabled), value);
    }

    public static Specification<User> type(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        var type = TypeUser.valueOf(value);
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.typeUser), type);
    }

    public static Specification<User> profile(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) -> {
            var join = root.join(User_.accessCard).join(AccessCard_.roles);

            return criteriaBuilder.like(
                criteriaBuilder.lower(join.get(Role_.name)),
                criteriaBuilder.lower(criteriaBuilder.literal(concatenateKeyValueWithWildCard(value)))
            );
        };
    }

    private static String concatenateKeyValueWithWildCard(String value) {
        return WILD_CARD + value.toLowerCase(Locale.getDefault()) + WILD_CARD;
    }
}
