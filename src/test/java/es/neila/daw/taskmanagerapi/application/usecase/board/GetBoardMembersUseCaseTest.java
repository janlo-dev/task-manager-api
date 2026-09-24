package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.BoardMemberDetailsResponse;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class GetBoardMembersUseCaseTest {

    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private GetBoardMembersUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();

    @Test
    void execute_returnsMembersWithUserDetails() {
        User ana = new User(requesterId, "Ana", "ana@test.com", "hash");
        BoardMember owner = new BoardMember(UUID.randomUUID(), boardId, requesterId, BoardRole.OWNER);
        when(boardMemberRepository.findByBoardId(boardId)).thenReturn(List.of(owner));
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(ana));

        List<BoardMemberDetailsResponse> result = useCase.execute(boardId, requesterId);

        assertThat(result).containsExactly(new BoardMemberDetailsResponse(
                owner.getId(), requesterId, "ana@test.com", "Ana", BoardRole.OWNER));
    }

    @Test
    void execute_skipsMembersWhoseUserNoLongerExists() {
        UUID orphanUserId = UUID.randomUUID();
        User ana = new User(requesterId, "Ana", "ana@test.com", "hash");
        BoardMember owner = new BoardMember(UUID.randomUUID(), boardId, requesterId, BoardRole.OWNER);
        BoardMember orphan = new BoardMember(UUID.randomUUID(), boardId, orphanUserId, BoardRole.MEMBER);
        when(boardMemberRepository.findByBoardId(boardId)).thenReturn(List.of(owner, orphan));
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(ana));
        when(userRepository.findById(orphanUserId)).thenReturn(Optional.empty());

        List<BoardMemberDetailsResponse> result = useCase.execute(boardId, requesterId);

        assertThat(result).extracting(BoardMemberDetailsResponse::userId).containsExactly(requesterId);
    }

    @Test
    void execute_withoutAccess_throwsAndQueriesNothing() {
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, requesterId);

        assertThatThrownBy(() -> useCase.execute(boardId, requesterId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(boardMemberRepository, userRepository);
    }
}
