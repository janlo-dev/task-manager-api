package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByColumnId(UUID columnId);
}
