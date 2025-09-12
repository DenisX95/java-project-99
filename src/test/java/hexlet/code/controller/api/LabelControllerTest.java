package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelInputDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LabelControllerTest {

    private final WebApplicationContext wac;
    private final UserRepository userRepository;
    private final LabelMapper labelMapper;
    private final LabelRepository labelRepository;
    private final ModelGenerator modelGenerator;
    private final ObjectMapper om;

    private MockMvc mockMvc;
    private User testUser;
    private Label testLabel;
    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        labelRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        var label = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(label);

        var response = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        List<LabelDTO> labelDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = labelDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/" + testLabel.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName()));
    }

    @Test
    public void testCreate() throws Exception {
        var data = new LabelInputDTO();
        data.setName("New label");

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var label = labelRepository.findByName("New label").orElse(null);
        assertNotNull(label);
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new LabelInputDTO();
        data.setName("Updated Name");

        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var label = labelRepository.findById(testLabel.getId()).orElseThrow();
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(testLabel.getId())).isEqualTo(false);
    }

}
