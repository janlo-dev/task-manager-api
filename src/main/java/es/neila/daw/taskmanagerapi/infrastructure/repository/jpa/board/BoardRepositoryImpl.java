package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.BoardMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository jpaRepository;
    private final BoardMapper mapper;

    public BoardRepositoryImpl(BoardJpaRepository jpaRepository, BoardMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Board save(Board board) {
        BoardEntity entity = mapper.toEntity(board);
        BoardEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Board> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Board> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
