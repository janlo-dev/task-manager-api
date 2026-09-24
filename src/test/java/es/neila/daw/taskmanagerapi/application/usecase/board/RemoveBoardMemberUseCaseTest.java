package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.event.BoardMemberRemovedEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveBoardMemberUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RemoveBoardMemberUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final Board board = new Board(boardId, ownerId, "Tablero", 0);

    @Test
    void execute_byOwner_removesMemberAndPublishesRemovedThenAuditEvents() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        useCase.execute(boardId, memberId, ownerId);

        verify(boardMemberRepository).deleteByBoardIdAndUserId(boardId, memberId);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publish(captor.capture());
        List<Object> events = captor.getAllValues();

        assertThat(events.get(0)).isEqualTo(new BoardMemberRemovedEvent(boardId, memberId, ownerId));
        AuditDomainEvent audit = (AuditDomainEvent) events.get(1);
        assertThat(audit.entityId()).isEqualTo(boardId);
        assertThat(audit.action()).isEqualTo("MEMBER_REMOVED");
        assertThat(audit.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownBoard_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(boardId, memberId, ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(boardMemberRepository, eventPublisher);
    }

    @Test
    void execute_byNonOwner_throwsUnauthorized() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> useCase.execute(boardId, memberId, UUID.randomUUID()))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(boardMemberRepository, eventPublisher);
    }

    @Test
    void execute_ownerRemovingThemselves_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> useCase.execute(boardId, ownerId, ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(boardMemberRepository, eventPublisher);
    }
}
