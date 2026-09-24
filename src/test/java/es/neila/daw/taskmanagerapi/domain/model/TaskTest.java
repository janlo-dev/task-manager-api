package es.neila.daw.taskmanagerapi.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 10, 0);

    private final UUID taskId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID columnId = UUID.randomUUID();

    // Fechas en el pasado para poder comprobar que las mutaciones actualizan updatedAt
    private Task newTask() {
        return new Task(taskId, "Título", "Descripción", userId, columnId, CREATED_AT, UPDATED_AT);
    }

    @Test
    void shortConstructor_setsTimestampsToNow() {
        LocalDateTime before = LocalDateTime.now();

        Task task = new Task(taskId, "Título", "Descripción", userId, columnId);

        LocalDateTime after = LocalDateTime.now();
        assertThat(task.getId()).isEqualTo(taskId);
        assertThat(task.getTitle()).isEqualTo("Título");
        assertThat(task.getDescription()).isEqualTo("Descripción");
        assertThat(task.getAssignedUserId()).isEqualTo(userId);
        assertThat(task.getColumnId()).isEqualTo(columnId);
        assertThat(task.getCreatedAt()).isBetween(before, after);
        assertThat(task.getUpdatedAt()).isBetween(before, after);
    }

    @Test
    void fullConstructor_keepsGivenTimestamps() {
        Task task = newTask();

        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(task.getUpdatedAt()).isEqualTo(UPDATED_AT);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shortConstructor_withBlankTitle_throwsException(String title) {
        assertThatThrownBy(() -> new Task(taskId, title, "Descripción", userId, columnId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void fullConstructor_withBlankTitle_throwsException(String title) {
        assertThatThrownBy(() -> new Task(taskId, title, "Descripción", userId, columnId, CREATED_AT, UPDATED_AT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rename_withValidTitle_changesTitleAndUpdatedAt() {
        Task task = newTask();

        task.rename("Nuevo título");

        assertThat(task.getTitle()).isEqualTo("Nuevo título");
        assertThat(task.getUpdatedAt()).isAfter(UPDATED_AT);
        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rename_withBlankTitle_throwsExceptionAndKeepsState(String newTitle) {
        Task task = newTask();

        assertThatThrownBy(() -> task.rename(newTitle))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(task.getTitle()).isEqualTo("Título");
        assertThat(task.getUpdatedAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void updateDescription_changesDescriptionAndUpdatedAt() {
        Task task = newTask();

        task.updateDescription("Otra descripción");

        assertThat(task.getDescription()).isEqualTo("Otra descripción");
        assertThat(task.getUpdatedAt()).isAfter(UPDATED_AT);
        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void updateDescription_acceptsNull() {
        Task task = newTask();

        task.updateDescription(null);

        assertThat(task.getDescription()).isNull();
    }

    @Test
    void moveToColumn_changesColumnAndUpdatedAt() {
        Task task = newTask();
        UUID newColumnId = UUID.randomUUID();

        task.moveToColumn(newColumnId);

        assertThat(task.getColumnId()).isEqualTo(newColumnId);
        assertThat(task.getUpdatedAt()).isAfter(UPDATED_AT);
        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void assignTo_changesAssignedUserAndUpdatedAt() {
        Task task = newTask();
        UUID otherUserId = UUID.randomUUID();

        task.assignTo(otherUserId);

        assertThat(task.getAssignedUserId()).isEqualTo(otherUserId);
        assertThat(task.getUpdatedAt()).isAfter(UPDATED_AT);
        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void unassign_clearsAssignedUserAndUpdatesUpdatedAt() {
        Task task = newTask();

        task.unassign();

        assertThat(task.getAssignedUserId()).isNull();
        assertThat(task.getUpdatedAt()).isAfter(UPDATED_AT);
        assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
    }
}
