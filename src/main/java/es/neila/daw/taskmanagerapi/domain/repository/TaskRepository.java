package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);
    Optional<Task> findById(UUID id);
    List<Task> findByColumnId(UUID columnId);
    void delete(UUID id);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.columnId IN :columnIds")
    void deleteByColumnIds(List<UUID> columnIds);
}
