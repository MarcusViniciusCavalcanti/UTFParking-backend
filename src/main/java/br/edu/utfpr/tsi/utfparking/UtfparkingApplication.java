package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.config.PropertiesDevelopment;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.repositories.RoleRepository;
import br.edu.utfpr.tsi.utfparking.structure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(value = {
        JwtConfiguration.class,
        PropertiesDevelopment.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UtfparkingApplication implements CommandLineRunner {

    @Autowired
    private PropertiesDevelopment propertiesDevelopment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

    @Override
    public void run(String... args) {

        if(propertiesDevelopment.isMock()) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("############################### Criando mock ######################################");

            List<User> users = List.of(
                    createUsers("Fulano Admin", "fulano_admin", List.of(1L, 2L)),
                    createUsers("Fulano Operator", "fulano_operator", List.of(1L, 3L)),
                    createUsers("Fulano User", "fulano_user", List.of(1L))
            );

            userRepository.saveAll(users);
            userRepository.flush();

            System.out.println();
            System.out.println();
            System.out.println();

            System.out.println(
                    "mock fulano_admin com senha 12345678 criado\n" +
                    "mock fulano_operator com senha 12345678 criado\n" +
                    "mock fulano_user com senha 12345678 criado\n"
            );

            System.out.println();
            System.out.println();
            System.out.println();


            System.out.println("########################## finalizado crianção de mock ############################");
            System.out.println("-----------------------------------------------------------------------------------");
        } else {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println();
            System.out.println();
            System.out.println("############################# Nenhum mock criado ###################################");
            System.out.println();
            System.out.println();
            System.out.println("-----------------------------------------------------------------------------------");
        }
    }

    private User createUsers(String name, String username, List<Long> roles) {
        List<Role> role = roleRepository.findAllById(roles);

        AccessCard accessCard = AccessCard.builder()
                .username("username")
                .password("123456789")
                .roles(role)
                .build();

        User user = User.builder()
                .name("Name")
                .accessCard(accessCard)
                .build();

        return user;
    }
}
