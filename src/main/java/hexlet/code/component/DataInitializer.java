package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.service.UserService;
import hexlet.code.util.TaskStatusUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final TaskStatusUtils taskStatusUtils;
        @Override
        public void run(ApplicationArguments args) throws Exception {
            UserCreateDTO userData = UserCreateDTO.builder()
                    .email("hexlet@example.com")
                    .password("qwerty")
                    .build();
            userService.create(userData);

            taskStatusUtils.create("Draft", "draft");
            taskStatusUtils.create("ToReview", "to_review");
            taskStatusUtils.create("ToBeFixed", "to_be_fixed");
            taskStatusUtils.create("ToPublished", "to_publish");
            taskStatusUtils.create("Published", "published");
    }
}
