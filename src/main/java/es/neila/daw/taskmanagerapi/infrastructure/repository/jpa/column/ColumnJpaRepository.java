package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ColumnJpaRepository extends JpaRepository<ColumnEntity, UUID> {

    List<ColumnEntity> findByBoardId(UUID boardId);

    @Query("SELECT c.id FROM ColumnEntity c WHERE c.boardId = :boardId")
    List<UUID> findIdsByBoardId(@Param("boardId") UUID boardId);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.columnId IN :columnIds")
    void deleteAllByIdIn(@Param("columnIds") List<UUID> columnIds);
}
