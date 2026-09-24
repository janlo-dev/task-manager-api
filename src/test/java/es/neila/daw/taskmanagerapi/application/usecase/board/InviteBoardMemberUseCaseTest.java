package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.InviteBoardMemberRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
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
class InviteBoardMemberUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private InviteBoardMemberUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final Board board = new Board(boardId, ownerId, "Tablero", 0);
    private final User invited = new User(UUID.randomUUID(), "Carla", "carla@test.com", "hash");
    private final InviteBoardMemberRequest request = new InviteBoardMemberRequest(boardId, "carla@test.com");

    @Test
    void execute_byOwner_savesMemberAndPublishesEvent() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findByEmail("carla@test.com")).thenReturn(Optional.of(invited));
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, invited.getId())).thenReturn(Optional.empty());
        when(boardMemberRepository.save(any(BoardMember.class))).then(returnsFirstArg());

        BoardMember result = useCase.execute(request, ownerId);

        assertThat(result.getBoardId()).isEqualTo(boardId);
        assertThat(result.getUserId()).isEqualTo(invited.getId());
        assertThat(result.getRole()).isEqualTo(BoardRole.MEMBER);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(boardId);
        assertThat(event.action()).isEqualTo("MEMBER_ADDED");
        assertThat(event.boardId()).isEqualTo(boardId);
        assertThat(event.performedBy()).isEqualTo(ownerId);
    }

    @Test
    void execute_withUnknownBoard_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(boardMemberRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_byNonOwner_throwsUnauthorized() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> useCase.execute(request, UUID.randomUUID()))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(userRepository, boardMemberRepository, eventPublisher);
    }

    @Test
    void execute_withUnknownEmail_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findByEmail("carla@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, ownerId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(boardMemberRepository, eventPublisher);
    }

    @Test
    void execute_withExistingMember_throws() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(userRepository.findByEmail("carla@test.com")).thenReturn(Optional.of(invited));
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, invited.getId()))
                .thenReturn(Optional.of(new BoardMember(UUID.randomUUID(), boardId, invited.getId(), BoardRole.MEMBER)));

        assertThatThrownBy(() -> useCase.execute(request, ownerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is already a member of this board");

        verify(boardMemberRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
