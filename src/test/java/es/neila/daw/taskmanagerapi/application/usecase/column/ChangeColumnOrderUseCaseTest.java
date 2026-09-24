package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeColumnOrderUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private ChangeColumnOrderUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    // Board con cuatro columnas en orden [0, 1, 2, 3]
    private Column c0;
    private Column c1;
    private Column c2;
    private Column c3;

    @BeforeEach
    void setUp() {
        c0 = new Column(boardId, UUID.randomUUID(), "C0", 0);
        c1 = new Column(boardId, UUID.randomUUID(), "C1", 1);
        c2 = new Column(boardId, UUID.randomUUID(), "C2", 2);
        c3 = new Column(boardId, UUID.randomUUID(), "C3", 3);
    }

    private void givenBoardColumns() {
        when(columnRepository.findByBoardIdOrderByColumnOrderAsc(boardId)).thenReturn(List.of(c0, c1, c2, c3));
        when(columnRepository.save(any(Column.class))).then(returnsFirstArg());
    }

    @Test
    void execute_movingForward_shiftsColumnsInBetweenBack() {
        when(columnRepository.findById(c0.getId())).thenReturn(Optional.of(c0));
        givenBoardColumns();

        Column result = useCase.execute(new ChangeColumnOrderRequest(c0.getId(), 2), userId);

        assertThat(result.getColumnOrder()).isEqualTo(2);
        assertThat(c1.getColumnOrder()).isEqualTo(0);
        assertThat(c2.getColumnOrder()).isEqualTo(1);
        assertThat(c3.getColumnOrder()).isEqualTo(3);
        verify(columnRepository, never()).save(c3);
    }

    @Test
    void execute_movingBackward_shiftsColumnsInBetweenForward() {
        when(columnRepository.findById(c3.getId())).thenReturn(Optional.of(c3));
        givenBoardColumns();

        Column result = useCase.execute(new ChangeColumnOrderRequest(c3.getId(), 1), userId);

        assertThat(result.getColumnOrder()).isEqualTo(1);
        assertThat(c0.getColumnOrder()).isEqualTo(0);
        assertThat(c1.getColumnOrder()).isEqualTo(2);
        assertThat(c2.getColumnOrder()).isEqualTo(3);
        verify(columnRepository, never()).save(c0);
    }

    @Test
    void execute_toSameOrder_onlySavesThatColumn() {
        when(columnRepository.findById(c1.getId())).thenReturn(Optional.of(c1));
        when(columnRepository.save(any(Column.class))).then(returnsFirstArg());

        useCase.execute(new ChangeColumnOrderRequest(c1.getId(), 1), userId);

        verify(columnRepository, never()).findByBoardIdOrderByColumnOrderAsc(any());
        verify(columnRepository, times(1)).save(any());
        verify(columnRepository).save(c1);
    }

    @Test
    void execute_publishesReorderedEvent() {
        when(columnRepository.findById(c0.getId())).thenReturn(Optional.of(c0));
        givenBoardColumns();

        useCase.execute(new ChangeColumnOrderRequest(c0.getId(), 2), userId);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(c0.getId());
        assertThat(event.entityType()).isEqualTo("COLUMN");
        assertThat(event.action()).isEqualTo("REORDERED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownColumn_throws() {
        UUID unknownId = UUID.randomUUID();
        when(columnRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ChangeColumnOrderRequest(unknownId, 2), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(columnRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndSavesNothing() {
        when(columnRepository.findById(c0.getId())).thenReturn(Optional.of(c0));
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(new ChangeColumnOrderRequest(c0.getId(), 2), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        assertThat(c0.getColumnOrder()).isZero();
        verify(columnRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
