package es.neila.daw.taskmanagerapi.domain.repository;

import es.neila.daw.taskmanagerapi.domain.model.BoardMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardMemberRepository {

    BoardMember save(BoardMember boardMember);
    Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId);
    List<BoardMember> findByBoardId(UUID boardId);
    void deleteByBoardIdAndUserId(UUID boardId, UUID userId);
    void deleteByBoardId(UUID boardId);
    List<BoardMember> findByUserId(UUID userId);
}
