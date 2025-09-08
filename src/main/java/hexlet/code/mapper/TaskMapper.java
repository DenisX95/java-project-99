package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    public abstract Task map(TaskCreateDTO data);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus", qualifiedByName = ("statusToSlug"))
    public abstract TaskDTO map(Task data);

    public abstract Task map(TaskDTO data);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToStatus")
    public abstract void update(TaskUpdateDTO data, @MappingTarget Task task);


    @Named("slugToStatus")
    public TaskStatus slugToStatus(String data) {
        var status = taskStatusRepository.findBySlug(data).orElseThrow();
        return status;
    }

    @Named("statusToSlug")
    public String statusToSlug(TaskStatus data) {
        var slug = data.getSlug();
        return slug;
    }
}
