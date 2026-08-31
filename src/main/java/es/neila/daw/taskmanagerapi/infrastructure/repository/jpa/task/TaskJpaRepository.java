package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.task;

import es.neila.daw.taskmanagerapi.application.dto.UserTaskProjectionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findByColumnId(UUID columnId);

    List<TaskEntity> findByColumnIdOrderByPositionAsc(UUID columnId);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.columnId IN :columnIds")
    void deleteByColumnIdIn(@Param("columnIds") List<UUID> columnIds);

    @Query("""
        SELECT 
            t.id AS taskId,
            t.title AS taskTitle,
            t.description AS taskDescription,
            c.id AS columnId,
            c.name AS columnName,
            b.id AS boardId,
            b.name AS boardName,
            t.createdAt AS createdAt
        FROM TaskEntity t
        JOIN ColumnEntity c ON t.columnId = c.id
        JOIN BoardEntity b ON c.boardId = b.id
        WHERE t.assignedUserId = :userId
        ORDER BY t.createdAt DESC
    """)
    List<UserTaskProjectionResponse> findTasksByAssignedUserId(@Param("userId") UUID userId);
}
