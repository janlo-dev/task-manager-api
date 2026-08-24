package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, UUID> {

    List<BoardEntity> findByUserId(UUID userId);
}
