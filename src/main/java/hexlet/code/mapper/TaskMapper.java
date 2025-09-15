package hexlet.code.mapper;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToId")
    public abstract TaskDTO map(Task data);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idToLabels")
    public abstract Task map(TaskCreateDTO data);

    public abstract Task map(TaskDTO data);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idToLabels")
    public abstract void update(TaskUpdateDTO data, @MappingTarget Task task);

    @Named("slugToTaskStatus")
    public final TaskStatus slugToStatus(String data) {
        return taskStatusRepository.findBySlug(data).orElseThrow();
    }

    @Named("labelsToId")
    public final List<Long> toDTO(Set<Label> labels) {
        return labels.isEmpty() ? new ArrayList<>() : labels.stream()
                .map(Label::getId)
                .collect(Collectors.toList());
    }

    @Named("idToLabels")
    public final List<Label> toEntity(List<Long> taskLabelIds) {
        return taskLabelIds.isEmpty() ? new ArrayList<>() : taskLabelIds.stream()
                .map(labelId -> labelRepository.findById(labelId).get())
                .collect(Collectors.toList());
    }
}
