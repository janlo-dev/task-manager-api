package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Column;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository {

    Column save(Column colum);
    Optional<Column> findById(UUID id);
    List<Column> findByBoardId(UUID boardId);
    void delete(UUID id);
}
