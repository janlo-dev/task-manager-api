package es.neila.daw.taskmanagerapi.infrastructure.mapper;

import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.task.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskEntity toEntity(Task task) {
        TaskEntity entity = new TaskEntity(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getColumnId()
        );

        entity.setCreatedAt(task.getCreatedAt());
        entity.setUpdatedAt(task.getUpdatedAt());

        return entity;
    }

    public Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getColumnId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
