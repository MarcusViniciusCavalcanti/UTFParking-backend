package br.edu.utfpr.tsi.utfparking.domain.users.spec;

import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.repositories.specs.BasicSpecification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Qualifier(UserSpecifications.QUALIFIER_GET_ALL_SPEC)
public class GetterAllUserSpec implements BasicSpecification<User, ParametersGetAllSpec> {

    @Override
    public Specification<User> filterBy(@NotNull final ParametersGetAllSpec spec) {
        return (root, query, builder) -> {
            query.distinct(true);
            return Specification.where(UserSpecifications.nameOrUsernameContains(spec.getName()))
                .and(UserSpecifications.enabled(spec.getEnabled()))
                .and(UserSpecifications.type(spec.getType()))
                .and(UserSpecifications.profile(spec.getProfile()))
                .toPredicate(root, query, builder);
        };
    }
}
