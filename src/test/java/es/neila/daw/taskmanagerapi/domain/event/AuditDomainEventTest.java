package es.neila.daw.taskmanagerapi.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditDomainEventTest {

    @Test
    void constructorWithoutTimestamp_setsTimestampToNow() {
        UUID entityId = UUID.randomUUID();
        UUID performedBy = UUID.randomUUID();
        LocalDateTime before = LocalDateTime.now();

        AuditDomainEvent event = new AuditDomainEvent(entityId, "TASK", "MOVED", performedBy, "detalle");

        LocalDateTime after = LocalDateTime.now();
        assertThat(event.entityId()).isEqualTo(entityId);
        assertThat(event.entityType()).isEqualTo("TASK");
        assertThat(event.action()).isEqualTo("MOVED");
        assertThat(event.performedBy()).isEqualTo(performedBy);
        assertThat(event.details()).isEqualTo("detalle");
        assertThat(event.timestamp()).isBetween(before, after);
    }
}
