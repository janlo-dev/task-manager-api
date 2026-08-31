package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.task;

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

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.columnId = :columnId")
    void deleteByColumnId(@Param("columnId") UUID columnId);
}
