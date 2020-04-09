package br.edu.utfpr.tsi.utfparking.domain.security.service;

import br.edu.utfpr.tsi.utfparking.structure.repositories.AccessCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccessCardRepository accessCardRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Searching in the DB the access card by username: '{}'", username);

        var accessCard = accessCardRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("this username '{}' not result", username);
                    return new UsernameNotFoundException(String.format("this username: '%s' not found", username));
                });

        log.info("access card to user: '{}'", accessCard.getUser().getName());

        return accessCard;
    }
}
