package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            UserCreateDTO userData = UserCreateDTO.builder()
                    .email("hexlet@example.com")
                    .password("qwerty")
                    .build();
            userService.create(userData);
    }
}
