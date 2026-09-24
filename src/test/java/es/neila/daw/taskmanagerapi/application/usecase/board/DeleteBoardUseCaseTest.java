package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.event.BoardDeletedEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
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
class DeleteBoardUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private DeleteBoardUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final Board board = new Board(boardId, ownerId, "Tablero", 0);

    @Test
    void execute_byOwner_deletesAndPublishesDeletedThenAuditEvents() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        useCase.execute(boardId, ownerId);

        verify(boardRepository).delete(boardId);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publish(captor.capture());
        List<Object> events = captor.getAllValues();

        assertThat(events.get(0)).isEqualTo(new BoardDeletedEvent(boardId, ownerId));
        AuditDomainEvent audit = (AuditDomainEvent) events.get(1);
        assertThat(audit.entityId()).isEqualTo(boardId);
        assertThat(audit.entityType()).isEqualTo("BOARD");
        assertThat(audit.action()).isEqualTo("DELETED");
        assertThat(audit.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownBoard_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(boardId, ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(boardRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_byNonOwner_throwsUnauthorizedAndDeletesNothing() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> useCase.execute(boardId, UUID.randomUUID()))
                .isInstanceOf(UnauthorizedActionException.class);

        verify(boardRepository, never()).delete(any());
        verifyNoInteractions(eventPublisher);
    }
}
