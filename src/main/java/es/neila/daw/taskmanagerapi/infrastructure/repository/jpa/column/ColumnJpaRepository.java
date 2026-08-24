package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ColumnJpaRepository extends JpaRepository<ColumnEntity, UUID> {

    List<ColumnEntity> findByBoardId(UUID boardId);
}
