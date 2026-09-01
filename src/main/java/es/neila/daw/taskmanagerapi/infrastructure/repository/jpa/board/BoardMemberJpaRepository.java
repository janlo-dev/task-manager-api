package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardMemberJpaRepository extends JpaRepository<BoardMemberEntity, UUID> {

    Optional<BoardMemberEntity> findByBoardIdAndUserId(UUID boardId, UUID userId);
    List<BoardMemberEntity> findByBoardId(UUID boardId);
    void deleteByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteByBoardId(UUID boardId);
}
