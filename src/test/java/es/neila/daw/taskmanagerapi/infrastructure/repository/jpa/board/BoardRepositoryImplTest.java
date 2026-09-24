package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.BoardMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BoardRepositoryImpl.class, BoardMapper.class})
class BoardRepositoryImplTest {

    @Autowired
    private BoardRepositoryImpl boardRepository;

    @Autowired
    private TestEntityManager em;

    private final UUID ana = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();

    private Board saveBoard(UUID ownerId, String name, int order) {
        return boardRepository.save(new Board(UUID.randomUUID(), ownerId, name, order));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindById_preservesAllFields() {
        Board board = saveBoard(ana, "Tablero", 3);
        flushAndClear();

        Board found = boardRepository.findById(board.getId()).orElseThrow();

        assertThat(found.getUserId()).isEqualTo(ana);
        assertThat(found.getName()).isEqualTo("Tablero");
        assertThat(found.getBoardOrder()).isEqualTo(3);
    }

    @Test
    void findById_withUnknownId_returnsEmpty() {
        assertThat(boardRepository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void findByUserId_returnsOnlyBoardsOwnedByThatUser() {
        saveBoard(ana, "Ana 1", 0);
        saveBoard(ana, "Ana 2", 1);
        saveBoard(carla, "Carla", 0);
        flushAndClear();

        assertThat(boardRepository.findByUserId(ana))
                .extracting(Board::getName)
                .containsExactlyInAnyOrder("Ana 1", "Ana 2");
    }

    @Test
    void save_existingBoard_updatesInsteadOfDuplicating() {
        Board board = saveBoard(ana, "Tablero", 0);
        flushAndClear();

        Board loaded = boardRepository.findById(board.getId()).orElseThrow();
        loaded.rename("Renombrado");
        boardRepository.save(loaded);
        flushAndClear();

        assertThat(boardRepository.findByUserId(ana))
                .singleElement()
                .extracting(Board::getName)
                .isEqualTo("Renombrado");
    }

    @Test
    void delete_removesOnlyThatBoard() {
        Board toDelete = saveBoard(ana, "Borrar", 0);
        Board toKeep = saveBoard(ana, "Mantener", 1);
        flushAndClear();

        boardRepository.delete(toDelete.getId());
        flushAndClear();

        assertThat(boardRepository.findById(toDelete.getId())).isEmpty();
        assertThat(boardRepository.findById(toKeep.getId())).isPresent();
    }
}
