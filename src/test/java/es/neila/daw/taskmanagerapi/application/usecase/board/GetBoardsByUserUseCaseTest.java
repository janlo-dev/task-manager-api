package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBoardsByUserUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;

    @InjectMocks
    private GetBoardsByUserUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_returnsBoardsOfUserMemberships() {
        Board own = new Board(UUID.randomUUID(), userId, "Propio", 0);
        Board shared = new Board(UUID.randomUUID(), UUID.randomUUID(), "Compartido", 0);
        when(boardMemberRepository.findByUserId(userId)).thenReturn(List.of(
                new BoardMember(UUID.randomUUID(), own.getId(), userId, BoardRole.OWNER),
                new BoardMember(UUID.randomUUID(), shared.getId(), userId, BoardRole.MEMBER)));
        when(boardRepository.findById(own.getId())).thenReturn(Optional.of(own));
        when(boardRepository.findById(shared.getId())).thenReturn(Optional.of(shared));

        List<Board> result = useCase.execute(userId);

        assertThat(result).containsExactly(own, shared);
    }

    @Test
    void execute_withoutMemberships_returnsEmptyList() {
        when(boardMemberRepository.findByUserId(userId)).thenReturn(List.of());

        assertThat(useCase.execute(userId)).isEmpty();
        verifyNoInteractions(boardRepository);
    }

    @Test
    void execute_withMembershipOfMissingBoard_throws() {
        UUID missingBoardId = UUID.randomUUID();
        when(boardMemberRepository.findByUserId(userId)).thenReturn(List.of(
                new BoardMember(UUID.randomUUID(), missingBoardId, userId, BoardRole.MEMBER)));
        when(boardRepository.findById(missingBoardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(userId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
