package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.repository.TaskRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.model.User;
import hexlet.code.dto.UserDTO;
import hexlet.code.util.ModelGenerator;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    private JwtRequestPostProcessor token;
    private User testUser;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testShow() throws Exception {
        var request =  get("/api/users/" + testUser.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("createdAt").isEqualTo(testUser.getCreatedAt().toString()),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    public void testIndex() throws Exception {
        var request =  get("/api/users").with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> actual = om.readValue(body, new TypeReference<java.util.List<UserDTO>>() { });
        List<UserDTO> expected = userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
        assertThat(user.getEmail()).isEqualTo(data.getEmail());
        assertThat(encoder.matches(data.getPassword(), user.getPasswordDigest())).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("email", "jack@yahoo.com");
        data.put("password", "new-password");

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getEmail()).isEqualTo(("jack@yahoo.com"));
        assertThat(encoder.matches("new-password", user.getPasswordDigest())).isTrue();
        assertThat(user.getFirstName()).isNotNull();
        assertThat(user.getLastName()).isNotNull();
    }

    @Test
    public void testDelete() throws Exception {
        var id = testUser.getId();
        var request = delete("/api/users/" + id).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    void testBlankRequiredFields() throws Exception {
        var data = new HashMap<String, Object>();
        data.put("email", "");
        data.put("password", "");

        mockMvc.perform(post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test403Exception() throws Exception {
        var userId = testUser.getId();
        mockMvc.perform(put("/api/users/{id}", userId + 1)
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"email": "new@example.com", "password": "newPassword"}
                    """))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginSuccess() throws Exception {
        var dataCreate = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();
        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataCreate));
        mockMvc.perform(request);

        var dataLogin = new HashMap<>();
        dataLogin.put("username", dataCreate.getEmail());
        dataLogin.put("password", dataCreate.getPassword());
        mockMvc.perform(post("/api/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(dataLogin)))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin401() throws Exception {
        var dataCreate = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();
        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataCreate));
        mockMvc.perform(request);

        var dataLogin = new HashMap<>();
        dataLogin.put("username", dataCreate.getEmail());
        dataLogin.put("password", dataCreate.getPassword() + "Labubu");
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dataLogin)))
                .andExpect(status().isUnauthorized());
    }
}
