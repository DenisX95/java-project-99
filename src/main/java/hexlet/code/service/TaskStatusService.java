package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

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

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        taskStatusMapper.update(taskStatusData, taskStatus);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
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

