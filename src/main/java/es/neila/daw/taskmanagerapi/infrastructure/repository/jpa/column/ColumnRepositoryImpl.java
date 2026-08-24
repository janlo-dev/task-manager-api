package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column;

import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.ColumnMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ColumnRepositoryImpl implements ColumnRepository {

    private final ColumnJpaRepository jpaRepository;
    private final ColumnMapper mapper;

    public ColumnRepositoryImpl(ColumnJpaRepository jpaRepository, ColumnMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Column save(Column colum) {
        ColumnEntity entity = mapper.toEntity(colum);
        ColumnEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Column> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Column> findByBoardId(UUID boardId) {
        return jpaRepository.findByBoardId(boardId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
