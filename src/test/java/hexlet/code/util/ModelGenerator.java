package hexlet.code.util;

import hexlet.code.dto.UserCreateDTO;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

import lombok.Getter;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import hexlet.code.model.User;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .generate(Select.field(User::getPasswordDigest), gen -> gen.string().length(10))
                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .generate(Select.field(UserCreateDTO::getPassword), gen -> gen.string().length(10))
                .toModel();
    }
}
