package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.impl.TaskStatusServiceImpl;
import hexlet.code.util.ModelGenerator;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskControllerTest {

    private final WebApplicationContext wac;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final TaskStatusServiceImpl taskStatusService;
    private final ObjectMapper om;
    private final TaskMapper taskMapper;
    private final ModelGenerator modelGenerator;

    private MockMvc mockMvc;
    private JwtRequestPostProcessor token;
    private Task testTask;
    private TaskStatus testTaskStatus;
    private User testUser;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.getLabels().add(testLabel);
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        List<TaskDTO> taskDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexWithPathParams() throws Exception {
        var testTask2 = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask2.setAssignee(testUser);
        testTask2.setTaskStatus(testTaskStatus);
        testTask.getLabels().add(testLabel);
        taskRepository.save(testTask2);

        var key1 = testTask.getName().substring(2);
        var key2 = testTask.getAssignee().getId();
        var key3 = testTask.getTaskStatus().getSlug();
        var key4 = testLabel.getId();

        var request = get("/api/tasks?"
                + "titleCont=" + key1
                + "&assigneeId=" + key2
                + "&status=" + key3
                + "&labelId=" + key4).with(token);

        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<TaskDTO> actual = om.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(1);
        assertThatJson(actual.get(0)).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()));
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()));
    }

    @Test
    public void testCreate() throws Exception {
        var slug = testTaskStatus.getSlug();
        var data = new TaskCreateDTO();
        data.setTitle("New Task");
        data.setStatus(slug);

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName("New Task").orElse(null);
        assertNotNull(task);
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(slug);
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new TaskUpdateDTO();
        data.setTitle(JsonNullable.of("Updated Title"));

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(task.getName()).isEqualTo(data.getTitle().get());
        assertNotNull(task.getDescription());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}
