package hexlet.code.util;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskStatusUtils {
    private final TaskStatusService taskStatusService;

    public void create(String name, String slug) {
        var taskStatusCreateDTO = new TaskStatusCreateDTO();
        taskStatusCreateDTO.setName(name);
        taskStatusCreateDTO.setSlug(slug);
        taskStatusService.create(taskStatusCreateDTO);
    }
}
