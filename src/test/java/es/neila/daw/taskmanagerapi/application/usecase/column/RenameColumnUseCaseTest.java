package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenameColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private RenameColumnUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);

    @Test
    void execute_withAccess_renamesSavesAndPublishesEvent() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(any(Column.class))).then(returnsFirstArg());

        Column result = useCase.execute(new RenameColumnRequest(column.getId(), "Done"), userId);

        assertThat(result.getName()).isEqualTo("Done");
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityType()).isEqualTo("COLUMN");
        assertThat(event.action()).isEqualTo("RENAMED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RenameColumnRequest(column.getId(), "Done"), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(columnRepository, never()).save(any());
        verifyNoInteractions(boardAccessChecker, eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndSavesNothing() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(new RenameColumnRequest(column.getId(), "Done"), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        assertThat(column.getName()).isEqualTo("To do");
        verify(columnRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
