package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.audit;

import es.neila.daw.taskmanagerapi.domain.model.AuditLog;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.AuditLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuditLogRepositoryAdapter.class, AuditLogMapper.class})
class AuditLogRepositoryAdapterTest {

    private static final LocalDateTime T1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 1, 2, 10, 0);
    private static final LocalDateTime T3 = LocalDateTime.of(2025, 1, 3, 10, 0);

    @Autowired
    private AuditLogRepositoryAdapter auditLogRepository;

    @Autowired
    private TestEntityManager em;

    private final UUID boardId = UUID.randomUUID();
    private final UUID taskId = UUID.randomUUID();
    private final UUID ana = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();

    private AuditLog saveLog(UUID entityId, UUID logBoardId, String action, UUID performedBy, LocalDateTime timestamp) {
        return auditLogRepository.save(new AuditLog(UUID.randomUUID(), entityId, "TASK", logBoardId, action,
                performedBy, "detalle " + action, timestamp));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindByEntityId_preservesAllFieldsIncludingBoardId() {
        AuditLog saved = saveLog(taskId, boardId, "CREATED", ana, T1);
        flushAndClear();

        assertThat(auditLogRepository.findByEntityId(taskId)).containsExactly(saved);
    }

    @Test
    void save_withNullBoardId_keepsItNull() {
        // Caso de los eventos de USER y de los registros anteriores a guardar el board
        saveLog(taskId, null, "CREATED", ana, T1);
        flushAndClear();

        assertThat(auditLogRepository.findByEntityId(taskId))
                .singleElement()
                .extracting(AuditLog::boardId)
                .isNull();
    }

    @Test
    void findByEntityId_returnsOnlyThatEntityNewestFirst() {
        saveLog(taskId, boardId, "CREATED", ana, T1);
        saveLog(taskId, boardId, "MOVED", carla, T3);
        saveLog(UUID.randomUUID(), boardId, "OTHER", ana, T2);
        saveLog(taskId, boardId, "RENAMED", ana, T2);
        flushAndClear();

        assertThat(auditLogRepository.findByEntityId(taskId))
                .extracting(AuditLog::action)
                .containsExactly("MOVED", "RENAMED", "CREATED");
    }

    @Test
    void findByPerformedBy_returnsOnlyThatUserNewestFirst() {
        saveLog(taskId, boardId, "CREATED", ana, T1);
        saveLog(UUID.randomUUID(), boardId, "RENAMED", ana, T3);
        saveLog(taskId, boardId, "MOVED", carla, T2);
        flushAndClear();

        assertThat(auditLogRepository.findByPerformedBy(ana))
                .extracting(AuditLog::action)
                .containsExactly("RENAMED", "CREATED");
    }

    @Test
    void findByEntityId_withoutLogs_returnsEmptyList() {
        assertThat(auditLogRepository.findByEntityId(UUID.randomUUID())).isEmpty();
    }
}
