package es.neila.daw.taskmanagerapi.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnTest {

    private final UUID boardId = UUID.randomUUID();
    private final UUID columnId = UUID.randomUUID();

    private Column newColumn() {
        return new Column(boardId, columnId, "To do", 1);
    }

    @Test
    void constructor_withValidData_assignsFields() {
        Column column = newColumn();

        assertThat(column.getBoardId()).isEqualTo(boardId);
        assertThat(column.getId()).isEqualTo(columnId);
        assertThat(column.getName()).isEqualTo("To do");
        assertThat(column.getColumnOrder()).isEqualTo(1);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void constructor_withBlankName_throwsException(String name) {
        assertThatThrownBy(() -> new Column(boardId, columnId, name, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_withNegativeOrder_throwsException() {
        assertThatThrownBy(() -> new Column(boardId, columnId, "To do", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column columnOrder cannot be negative");
    }

    @Test
    void constructor_withZeroOrder_isValid() {
        Column column = new Column(boardId, columnId, "To do", 0);

        assertThat(column.getColumnOrder()).isZero();
    }

    @Test
    void rename_withValidName_changesName() {
        Column column = newColumn();

        column.rename("Done");

        assertThat(column.getName()).isEqualTo("Done");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rename_withBlankName_throwsExceptionAndKeepsName(String newName) {
        Column column = newColumn();

        assertThatThrownBy(() -> column.rename(newName))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(column.getName()).isEqualTo("To do");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void changeOrder_withNonNegativeOrder_changesOrder(int newOrder) {
        Column column = newColumn();

        column.changeOrder(newOrder);

        assertThat(column.getColumnOrder()).isEqualTo(newOrder);
    }

    @Test
    void changeOrder_withNegativeOrder_throwsExceptionAndKeepsOrder() {
        Column column = newColumn();

        assertThatThrownBy(() -> column.changeOrder(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column columnOrder cannot be negative");
        assertThat(column.getColumnOrder()).isEqualTo(1);
    }
}
