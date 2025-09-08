package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final TaskStatusService taskStatusService;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            UserCreateDTO userData = UserCreateDTO.builder()
                    .email("hexlet@example.com")
                    .password("qwerty")
                    .build();
            userService.create(userData);

            taskStatusService.create("Draft", "draft");
            taskStatusService.create("ToReview", "to_review");
            taskStatusService.create("ToBeFixed", "to_be_fixed");
            taskStatusService.create("ToPublished", "to_publish");
            taskStatusService.create("Published", "published");
    }
}
