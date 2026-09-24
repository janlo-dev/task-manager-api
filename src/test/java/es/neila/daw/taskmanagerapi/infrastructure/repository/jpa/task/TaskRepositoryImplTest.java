package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.task;

import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TaskRepositoryImpl.class, TaskMapper.class})
class TaskRepositoryImplTest {

    private static final LocalDateTime T1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 1, 2, 10, 0);
    private static final LocalDateTime T3 = LocalDateTime.of(2025, 1, 3, 10, 0);

    @Autowired
    private TaskRepositoryImpl taskRepository;

    @Autowired
    private TestEntityManager em;

    private final UUID columnA = UUID.randomUUID();
    private final UUID columnB = UUID.randomUUID();
    private final UUID ana = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();

    private Task saveTask(String title, UUID columnId, UUID assignedUserId, LocalDateTime createdAt) {
        return taskRepository.save(new Task(UUID.randomUUID(), title, "Desc", assignedUserId, columnId, createdAt, createdAt));
    }

    // Fuerza a que las lecturas vayan a la BD y no a la caché de primer nivel
    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindById_preservesAllFields() {
        Task task = new Task(UUID.randomUUID(), "Título", "Desc", ana, columnA, T1, T2);
        taskRepository.save(task);
        flushAndClear();

        Task found = taskRepository.findById(task.getId()).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("Título");
        assertThat(found.getDescription()).isEqualTo("Desc");
        assertThat(found.getAssignedUserId()).isEqualTo(ana);
        assertThat(found.getColumnId()).isEqualTo(columnA);
        assertThat(found.getCreatedAt()).isEqualTo(T1);
        assertThat(found.getUpdatedAt()).isEqualTo(T2);
    }

    @Test
    void save_withoutAssignedUser_keepsItNull() {
        Task task = saveTask("Sin asignar", columnA, null, T1);
        flushAndClear();

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getAssignedUserId()).isNull();
    }

    @Test
    void findByColumnIdOrderByCreatedAtAsc_returnsOnlyThatColumnOldestFirst() {
        saveTask("tercera", columnA, null, T3);
        saveTask("primera", columnA, null, T1);
        saveTask("otra columna", columnB, null, T2);
        saveTask("segunda", columnA, null, T2);
        flushAndClear();

        List<Task> result = taskRepository.findByColumnIdOrderByCreatedAtAsc(columnA);

        assertThat(result).extracting(Task::getTitle).containsExactly("primera", "segunda", "tercera");
    }

    @Test
    void findByAssignedUserId_returnsOnlyTasksOfThatUser() {
        saveTask("de ana 1", columnA, ana, T1);
        saveTask("de ana 2", columnB, ana, T2);
        saveTask("de carla", columnA, carla, T1);
        flushAndClear();

        assertThat(taskRepository.findByAssignedUserId(ana))
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("de ana 1", "de ana 2");
    }

    @Test
    void deleteByColumnIds_deletesOnlyTasksOfThoseColumns() {
        Task inA = saveTask("en A", columnA, null, T1);
        Task inB = saveTask("en B", columnB, null, T1);
        UUID columnC = UUID.randomUUID();
        Task inC = saveTask("en C", columnC, null, T1);
        flushAndClear();

        taskRepository.deleteByColumnIds(List.of(columnA, columnC));
        flushAndClear();

        assertThat(taskRepository.findById(inA.getId())).isEmpty();
        assertThat(taskRepository.findById(inC.getId())).isEmpty();
        assertThat(taskRepository.findById(inB.getId())).isPresent();
    }

    @Test
    void unassignByColumnIdsAndUserId_unassignsOnlyThatUserInThoseColumns() {
        Task anaInA = saveTask("ana en A", columnA, ana, T1);
        Task anaInB = saveTask("ana en B (otro board)", columnB, ana, T1);
        Task carlaInA = saveTask("carla en A", columnA, carla, T1);
        flushAndClear();

        taskRepository.unassignByColumnIdsAndUserId(List.of(columnA), ana);
        flushAndClear();

        Task unassigned = taskRepository.findById(anaInA.getId()).orElseThrow();
        assertThat(unassigned.getAssignedUserId()).isNull();
        assertThat(unassigned.getUpdatedAt()).isAfter(T1);
        assertThat(unassigned.getCreatedAt()).isEqualTo(T1);

        Task untouchedOtherBoard = taskRepository.findById(anaInB.getId()).orElseThrow();
        assertThat(untouchedOtherBoard.getAssignedUserId()).isEqualTo(ana);
        assertThat(untouchedOtherBoard.getUpdatedAt()).isEqualTo(T1);

        Task untouchedOtherUser = taskRepository.findById(carlaInA.getId()).orElseThrow();
        assertThat(untouchedOtherUser.getAssignedUserId()).isEqualTo(carla);
        assertThat(untouchedOtherUser.getUpdatedAt()).isEqualTo(T1);
    }

    @Test
    void save_existingTask_updatesInsteadOfDuplicating() {
        Task task = saveTask("Título", columnA, null, T1);
        flushAndClear();

        Task loaded = taskRepository.findById(task.getId()).orElseThrow();
        loaded.rename("Renombrada");
        loaded.moveToColumn(columnB);
        taskRepository.save(loaded);
        flushAndClear();

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getTitle()).isEqualTo("Renombrada");
        assertThat(taskRepository.findByColumnIdOrderByCreatedAtAsc(columnA)).isEmpty();
        assertThat(taskRepository.findByColumnIdOrderByCreatedAtAsc(columnB)).hasSize(1);
    }

    @Test
    void delete_removesOnlyThatTask() {
        Task toDelete = saveTask("borrar", columnA, null, T1);
        Task toKeep = saveTask("mantener", columnA, null, T2);
        flushAndClear();

        taskRepository.delete(toDelete.getId());
        flushAndClear();

        assertThat(taskRepository.findById(toDelete.getId())).isEmpty();
        assertThat(taskRepository.findById(toKeep.getId())).isPresent();
    }
}
