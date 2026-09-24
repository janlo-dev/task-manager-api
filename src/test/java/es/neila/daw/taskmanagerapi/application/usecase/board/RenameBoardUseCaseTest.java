package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenameBoardUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RenameBoardUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final Board board = new Board(boardId, ownerId, "Tablero", 0);

    @Test
    void execute_byOwner_renamesSavesAndPublishesEvent() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).then(returnsFirstArg());

        Board result = useCase.execute(new RenameBoardRequest(boardId, "Nuevo"), ownerId);

        assertThat(result.getName()).isEqualTo("Nuevo");
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityType()).isEqualTo("BOARD");
        assertThat(event.action()).isEqualTo("RENAMED");
        assertThat(event.boardId()).isEqualTo(boardId);
        assertThat(event.performedBy()).isEqualTo(ownerId);
    }

    @Test
    void execute_withUnknownBoard_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RenameBoardRequest(boardId, "Nuevo"), ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(boardRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_byNonOwner_throwsUnauthorizedAndSavesNothing() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> useCase.execute(new RenameBoardRequest(boardId, "Nuevo"), UUID.randomUUID()))
                .isInstanceOf(UnauthorizedActionException.class);

        assertThat(board.getName()).isEqualTo("Tablero");
        verify(boardRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
