package br.edu.utfpr.tsi.utfparking;

import br.edu.utfpr.tsi.utfparking.domain.security.properties.JwtConfiguration;
import br.edu.utfpr.tsi.utfparking.domain.users.service.UserService;
import br.edu.utfpr.tsi.utfparking.structure.dtos.InputUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtConfiguration.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UtfparkingApplication implements CommandLineRunner {

    @Value("${isMock}")
    private boolean isMock;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(UtfparkingApplication.class, args);
    }

    @Override
    public void run(String... args) {

        if(isMock) {

            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("############################### Criando mock ######################################");

            userService.saveNewUser(createUsers("Fulano Admin", "fulano_admin", List.of(1L, 2L)));
            userService.saveNewUser(createUsers("Fulano Operator", "fulano_operator", List.of(1L, 3L)));
            userService.saveNewUser(createUsers("Fulano User", "fulano_user", List.of(1L)));

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

    private InputUserDTO createUsers(String name, String username, List<Long> roles) {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setName(name);
        inputUser.setUsername(username);
        inputUser.setPassword("123456789");
        inputUser.setAuthorities(roles);

        return inputUser;
    }
}
