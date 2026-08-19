package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findByColumdId(UUID columId);
    void delete(UUID id);

}
