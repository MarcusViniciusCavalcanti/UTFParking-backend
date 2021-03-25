package br.edu.utfpr.tsi.utfparking.structure.repositories.specs;

import org.springframework.data.jpa.domain.Specification;

public interface BasicSpecification<T, U> {

    Specification<T> filterBy(U object);

}

