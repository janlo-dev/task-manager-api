package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.Board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository {

    Board save(Board board);
    Optional<Board> findById(UUID id);
    List<Board> findByUserId(UUID userId);
    void delete(UUID id);
}
