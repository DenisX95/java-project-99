package hexlet.code.util;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

import lombok.Getter;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import hexlet.code.model.User;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    @PostConstruct
    private void init() {
        Faker faker = new Faker();

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getTasks))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .generate(Select.field(User::getPasswordDigest), gen -> gen.string().length(10))
                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .generate(Select.field(UserCreateDTO::getPassword), gen -> gen.string().length(10))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .ignore(Select.field(TaskStatus::getTasks))
                .generate(Select.field(TaskStatus::getName), gen -> gen.string().length(10))
                .generate(Select.field(TaskStatus::getSlug), gen -> gen.string().length(5))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getLabels))
                .ignore(Select.field(Task::getCreatedAt))
                .generate(Select.field(Task::getIndex), gen -> gen.ints().range(1, 100))
                .generate(Select.field(Task::getName), gen -> gen.string().minLength(1).maxLength(50))
                .generate(Select.field(Task::getDescription), gen -> gen.string().minLength(1).maxLength(200))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .ignore(Select.field(Label::getTasks))
                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
                .toModel();
    }
}
