package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Column;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository {

    Column save(Column colum);
    Optional<Column> findById(UUID id);
    void delete(UUID id);

    List<UUID> findIdsByBoardId(UUID boardId);
    void deleteAllByIds(List<UUID> columnIds);
    List<Column> findByBoardIdOrderByColumnOrderAsc(UUID boardId);
}
