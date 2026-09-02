package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.BoardMemberMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BoardMemberRepositoryImpl implements BoardMemberRepository {

    private final BoardMemberJpaRepository jpaRepository;
    private final BoardMemberMapper mapper;

    public BoardMemberRepositoryImpl(BoardMemberJpaRepository jpaRepository, BoardMemberMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public BoardMember save(BoardMember boardMember) {
        BoardMemberEntity entity = mapper.toEntity(boardMember);
        BoardMemberEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId) {
        return jpaRepository.findByBoardIdAndUserId(boardId, userId).map(mapper::toDomain);
    }

    @Override
    public List<BoardMember> findByBoardId(UUID boardId) {
        return jpaRepository.findByBoardId(boardId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByBoardIdAndUserId(UUID boardId, UUID userId) {
        jpaRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

    @Override
    public void deleteByBoardId(UUID boardId) {
        jpaRepository.deleteByBoardId(boardId);
    }

    @Override
    public List<BoardMember> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
