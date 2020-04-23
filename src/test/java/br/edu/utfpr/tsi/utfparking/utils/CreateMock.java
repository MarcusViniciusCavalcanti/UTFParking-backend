package br.edu.utfpr.tsi.utfparking.utils;

import br.edu.utfpr.tsi.utfparking.domain.security.entity.AccessCard;
import br.edu.utfpr.tsi.utfparking.domain.security.entity.Role;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.Car;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.TypeUser;
import br.edu.utfpr.tsi.utfparking.domain.users.entity.User;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUpdateCarDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.inputs.InputUserDTO;
import br.edu.utfpr.tsi.utfparking.structure.dtos.TypeUserDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMock {
    public static final String CAR_MODEL = "Gol";
    public static final String CAR_PLATE = "abc1234";
    public static final List<Long> AUTHORITIES_ADMIN = List.of(1L, 2L);
    public static final List<Long> AUTHORITIES_DRIVER = List.of(1L);
    public static final List<Long> AUTHORITIES_OPERATOR = List.of(1L, 3L);

    public static final String[] NAMES = {
            "Alessandro","Alexandre","Antônio","Arthur","Benício","Benjamin","Bernardo","Breno","Bryan",
            "Caio","Carlos","Cauã","César","Daniel","Danilo","Davi","Deneval","Djalma","Eduardo","Elísio",
            "Emanuel","Enrico","Enzo","Fabiano","Fábio","Fabrício","Feliciano","Felipe","Félix","Francisco",
            "Frederico","Gabriel","Gúbio","Guilherme","Gustavo","Heitor","Hélio","Henrique","Hugo","Ígor","Isaac",
            "João","Joaquim","Júlio","Kléber","Ladislau","Leonardo","Lorenzo","Lucas","Lucca","Marcelo","Márcio",
            "Marcos","Matheus","Miguel","Murilo","Nataniel","Nicolas","Norberto","Pablo","Paulo","Pedro",
            "Pietro","Rafael","Raul","Ricardo","Roberto","Salvador","Samuel","Silas","Sirineu","Tertuliano","Theo",
            "Thiago","Thomas","Vicente","Víctor","Vinicius","Vitor","Warley","Washington","Yago","Yango","Yuri",
            "André","Anthony","Arthur Gabriel","Arthur Henrique","Arthur Miguel","Augusto","Bento","Bruno","Calebe",
            "Carlos Eduardo","Davi Lucas","Davi Luca","Davi Lucca","Davi Ludmer","Davi Luiz","Davi Miguel",
            "Enzo Gabriel","Enzo Thiago","Enzo Miguel","Erick","Fernando","Gael","Henry","Ian","Igor","João",
            "João Gabriel","João Guilherme","João Lucas","João Luca","João Miguel","João Samuel","João Pedro",
            "João Felipe","João Vitor","João Victor","José","Kaique","Kauê","Levi","Luan","Lucas Gabriel","Luiz",
            "Alice","Alícia","Aline","Amanda","Ana","Beatriz","Bianca","Bruna","Carla","Catarina","Cecília",
            "Célia","Clara","Dalila","Eduarda","Emanuelly","Esther","Fabrícia","Felícia","Fernanda","Gabriela",
            "Giovanna","Helena","Heloísa","Isabel","Isabela","Isabella","Isabelly","Isadora","Isis","Janaína",
            "Joana","Júlia","Karla","Lara","Larissa","Laura","Lavínia","Letícia","Lívia","Lorena","Lorraine",
            "Luiza","Manuela","Marcela","Márcia","Margarida","Maria","Mariana","Marina","Marli","Meire","Melissa","Mércia",
            "Morgana","Natália","Nicole","Núbia","Ofélia","Paula","Rafaela","Rebeca","Roberta","Sara","Sarah",
            "Sílvia","Sophia","Suélen","Talita","Valentina","Vitória","Yasmin","Adriana","Agatha","Alessandra",
            "Allana","Ana Beatriz","Ana Cecília","Ana Clara","Ana Júlia","Ana Laura","Ana Lívia","Ana Luiza",
            "Ana Sophia","Ana Vitória","Antonella","Antonia","Aurora","Ayla","Bárbara","Camila","Carolina","Clarice",
            "Elisa","Eloah","Emilly","Francisca","Gabrielly","Giulia","Juliana","Laís","Liz","Louise","Luana",
            "Luna","Maitê","Malu","Marcia","Maria Alice","Maria Cecília","Maria Clara","Maria Eduarda",
            "Maria Fernanda","Maria Flor","Maria Helena","Maria Isis","Maria Júlia","Maria Laura","Maria Luiza",
            "Maria Sophia","Maria Valentina","Maria Vitória","Mariah","Maya","Milena","Mirella","Olívia","Patricia",
            "Pérola","Pietra","Sophie","Stella"
    };

    public static final String[] MODEL_CARS = {
            "MINI STAR UTILITY", "MINI STAR FAMILY", "HUMMER", "1INTEGRA", "LEGEND", "NSX", "MARRUA", "SPIDER",
            "AM-825", "HI-TOPIC", "ROCSTA", "TOPIC", "TOWNER", "A1", "A3", "A4 SEDAN", "A5 COUPE", "A6 SEDAN",
            "A7", "A8", "Q3", "Q5", "Q7", "R8", "RS3", "RS4", "RS5", "RS6", "S3", "S4 SEDAN", "S6 SEDAN", "S8",
            "TT", "TTS", "BUGGY", "DEVILLE", "ELDORADO", "SEVILLE", "JAVALI", "CIELO", "FACE", "QQ", "S-18",
            "TIGGO", "300C", "CARAVAN", "CIRRUS", "GRAN CARAVAN", "LE BARON", "NEON", "PT CRUISER", "SEBRING",
            "STRATUS", "TOWN E COUNTRY", "VISION", "AIRCROSS", "AX", "BERLINGO", "BX", "CL-330", "ESPERO", "LANOS",
            "LEGANZA", "NUBIRA", "PRINCE", "RACER", "SUPER SALON", "TICO", "APPLAUSE", "CHARADE", "C3", "C4", "C5",
            "C6", "C8", "DS3", "EVASION", "JUMPER", "XANTIA", "XM", "XSARA", "ZX", "CL-244"
    };

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type, List<Long> roles, String carModel, String carPlate) {
        var inputUser = new InputUserDTO();

        inputUser.setAccountNonExpired(true);
        inputUser.setAccountNonLocked(true);
        inputUser.setEnabled(true);
        inputUser.setType(type);
        inputUser.setName(name);
        inputUser.setUsername(username);
        inputUser.setPassword(pass);
        inputUser.setCarModel(carModel);
        inputUser.setCarPlate(carPlate);
        inputUser.setAuthorities(roles);

        return inputUser;
    }

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type) {
        return createMockInputUserDTO(name, username, pass, type, AUTHORITIES_ADMIN);
    }

    public static InputUpdateCarDTO createMockInputUpdateCarDTO() {
        var input = new InputUpdateCarDTO();

        input.setCarModel(CAR_MODEL);
        input.setCarPlate(CAR_PLATE);

        return input;
    }

    public static InputUserDTO createMockInputUserDTO(String name, String username, String pass, TypeUserDTO type, List<Long> roles) {
        return createMockInputUserDTO(name, username, pass, type, roles, CAR_MODEL, CAR_PLATE);
    }

    public static Role createRole(Long id, String description) {
        return createRole(id, description, "");
    }

    public static Role createRole(Long id, String description, String name) {
        return Role.builder()
                .id(id)
                .description(description)
                .name(name)
                .build();
    }

    public static AccessCard createAccessCard(Long id, List<Role> roles, String username, String password) {
        return AccessCard.builder()
                .roles(roles)
                .username(username)
                .password(password)
                .id(id)
                .build();
    }

    public static Car createCar(Long id, String model, String plate) {
        return Car.builder()
                .id(id)
                .model(model)
                .plate(plate)
                .build();
    }

    public static User createUser(Long id, AccessCard accessCard, TypeUser typeUser, String name, Car car) {
        var user = User.builder()
                .accessCard(accessCard)
                .typeUser(typeUser)
                .name(name)
                .id(id)
                .car(car)
                .build();

        if (car != null) {
            car.setUser(user);
        }
        return user;
    }

    public static User createUserDefaultUser() {
        var car = CreateMock.createCar(1L, "gol", "abc1234");
        var role = createRole(1L, "name");
        var accessCard = createAccessCard(1L, List.of(role), "username", "password");

        return createUser(1L, accessCard, TypeUser.STUDENTS, "name", car);
    }

    public static List<User> createListUser() {
        return LongStream.rangeClosed(0, 10)
                .boxed()
                .map(index -> {
                    var car = CreateMock.createCar(index, MODEL_CARS[new Random().nextInt(MODEL_CARS.length - 1)], "abc123" + index);
                    var role = createRole(index, "name");
                    var accessCard = createAccessCard(index, List.of(role), "username", "password");

                    return createUser(index, accessCard, TypeUser.STUDENTS, NAMES[new Random().nextInt(NAMES.length - 1)], car);
                }).collect(Collectors.toList());
    }

    public static List<InputUserDTO> createListInputUsersDTO() {
        return LongStream.rangeClosed(0, 10)
                .boxed()
                .map(index -> {
                    var inputUser = new InputUserDTO();

                    inputUser.setAccountNonExpired(true);
                    inputUser.setAccountNonLocked(true);
                    inputUser.setEnabled(true);
                    int value = index % 2 == 0 ? 0 : 1;
                    inputUser.setType(TypeUserDTO.values()[value]);
                    inputUser.setName(NAMES[new Random().nextInt(NAMES.length - 1)]);
                    inputUser.setUsername("username" + index);
                    inputUser.setPassword("password");
                    inputUser.setCarModel(MODEL_CARS[new Random().nextInt(MODEL_CARS.length - 1)]);
                    inputUser.setCarPlate("abcd123" + index);
                    inputUser.setAuthorities(index % 2 == 0 ? AUTHORITIES_DRIVER : AUTHORITIES_OPERATOR);

                    return inputUser;
                })
                .collect(Collectors.toList());
    }
}
