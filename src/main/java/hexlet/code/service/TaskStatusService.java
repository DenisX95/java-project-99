package hexlet.code.service;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusDTO> getAll();
    TaskStatusDTO findById(Long id);
    TaskStatusDTO create(TaskStatusCreateDTO taskStatusData);
    TaskStatusDTO create(String name, String slug);
    TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id);
    void delete(Long id);
}
