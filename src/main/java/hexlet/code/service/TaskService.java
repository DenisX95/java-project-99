package hexlet.code.service;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskParamsDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAll(TaskParamsDTO params);
    TaskDTO findById(Long id);
    TaskDTO create(TaskCreateDTO taskData);
    TaskDTO update(TaskUpdateDTO taskData, Long id);
    void delete(Long id);
}
