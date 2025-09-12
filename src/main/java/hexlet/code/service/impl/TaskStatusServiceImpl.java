package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;

import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = repository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        taskStatusMapper.update(taskStatusData, taskStatus);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public TaskStatusDTO create(String name, String slug) {
        var taskStatusCreateDTO = new TaskStatusCreateDTO();
        taskStatusCreateDTO.setName(name);
        taskStatusCreateDTO.setSlug(slug);
        var taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }
}

