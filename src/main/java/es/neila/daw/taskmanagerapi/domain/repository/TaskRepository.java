package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findByColumnIdOrderByCreatedAtAsc(UUID columnId);
    void delete(UUID id);
    void deleteByColumnIds(List<UUID> columnIds);
    List<Task> findByAssignedUserId(UUID userId);
    void unassignByColumnIdsAndUserId(List<UUID> columnIds, UUID userId);
}
