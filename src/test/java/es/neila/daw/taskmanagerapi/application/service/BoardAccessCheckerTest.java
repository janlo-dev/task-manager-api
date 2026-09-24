package es.neila.daw.taskmanagerapi.application.service;

import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardAccessCheckerTest {

    @Mock
    private BoardMemberRepository boardMemberRepository;

    @InjectMocks
    private BoardAccessChecker boardAccessChecker;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void verifyCanEditContent_withMember_doesNotThrow() {
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, userId))
                .thenReturn(Optional.of(new BoardMember(UUID.randomUUID(), boardId, userId, BoardRole.MEMBER)));

        assertThatCode(() -> boardAccessChecker.verifyCanEditContent(boardId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    void verifyCanEditContent_withNonMember_throwsUnauthorized() {
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardAccessChecker.verifyCanEditContent(boardId, userId))
                .isInstanceOf(UnauthorizedActionException.class);
    }
}
