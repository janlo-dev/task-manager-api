package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.event.ColumnDeletedEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private DeleteColumnUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);

    @Test
    void execute_withAccess_deletesAndPublishesDeletedThenAuditEvents() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        useCase.execute(column.getId(), userId);

        verify(columnRepository).delete(column.getId());

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publish(captor.capture());
        List<Object> events = captor.getAllValues();

        assertThat(events.get(0)).isEqualTo(new ColumnDeletedEvent(column.getId(), userId));
        AuditDomainEvent audit = (AuditDomainEvent) events.get(1);
        assertThat(audit.entityType()).isEqualTo("COLUMN");
        assertThat(audit.action()).isEqualTo("DELETED");
        assertThat(audit.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(column.getId(), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(columnRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndDeletesNothing() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(column.getId(), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verify(columnRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }
}
