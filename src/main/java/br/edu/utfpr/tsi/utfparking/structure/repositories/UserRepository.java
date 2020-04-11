package br.edu.utfpr.tsi.utfparking.structure.repositories;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccessCard(AccessCard accessCard);

    Boolean existsUserByAccessCardUsername(String username);
}
