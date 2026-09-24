package es.neila.daw.taskmanagerapi.domain.model;

import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    private final UUID boardId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();

    private Board newBoard() {
        return new Board(boardId, ownerId, "Tablero", 1);
    }

    @Test
    void constructor_withValidData_assignsFields() {
        Board board = newBoard();

        assertThat(board.getId()).isEqualTo(boardId);
        assertThat(board.getUserId()).isEqualTo(ownerId);
        assertThat(board.getName()).isEqualTo("Tablero");
        assertThat(board.getBoardOrder()).isEqualTo(1);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void constructor_withBlankName_throwsException(String name) {
        assertThatThrownBy(() -> new Board(boardId, ownerId, name, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_withNegativeOrder_throwsException() {
        assertThatThrownBy(() -> new Board(boardId, ownerId, "Tablero", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_withZeroOrder_isValid() {
        Board board = new Board(boardId, ownerId, "Tablero", 0);

        assertThat(board.getBoardOrder()).isZero();
    }

    @Test
    void rename_withValidName_changesName() {
        Board board = newBoard();

        board.rename("Nuevo nombre");

        assertThat(board.getName()).isEqualTo("Nuevo nombre");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rename_withBlankName_throwsExceptionAndKeepsName(String newName) {
        Board board = newBoard();

        assertThatThrownBy(() -> board.rename(newName))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(board.getName()).isEqualTo("Tablero");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void changeOrder_withNonNegativeOrder_changesOrder(int newOrder) {
        Board board = newBoard();

        board.changeOrder(newOrder);

        assertThat(board.getBoardOrder()).isEqualTo(newOrder);
    }

    @Test
    void changeOrder_withNegativeOrder_throwsExceptionAndKeepsOrder() {
        Board board = newBoard();

        assertThatThrownBy(() -> board.changeOrder(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(board.getBoardOrder()).isEqualTo(1);
    }

    @Test
    void verifyCanManage_withOwner_doesNotThrow() {
        Board board = newBoard();

        assertThatCode(() -> board.verifyCanManage(ownerId)).doesNotThrowAnyException();
    }

    @Test
    void verifyCanManage_withOtherUser_throwsUnauthorized() {
        Board board = newBoard();

        assertThatThrownBy(() -> board.verifyCanManage(UUID.randomUUID()))
                .isInstanceOf(UnauthorizedActionException.class);
    }
}
