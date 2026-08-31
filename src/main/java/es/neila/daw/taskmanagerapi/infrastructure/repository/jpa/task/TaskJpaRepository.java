package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByColumnId(UUID columnId);
    void deleteByColumnIdIn(@Param("columnIds") List<UUID> columnIds);
}
