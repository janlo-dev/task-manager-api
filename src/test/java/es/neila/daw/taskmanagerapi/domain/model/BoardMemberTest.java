package es.neila.daw.taskmanagerapi.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardMemberTest {

    private final UUID memberId = UUID.randomUUID();
    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void constructor_withValidData_assignsFields() {
        BoardMember member = new BoardMember(memberId, boardId, userId, BoardRole.MEMBER);

        assertThat(member.getId()).isEqualTo(memberId);
        assertThat(member.getBoardId()).isEqualTo(boardId);
        assertThat(member.getUserId()).isEqualTo(userId);
        assertThat(member.getRole()).isEqualTo(BoardRole.MEMBER);
    }

    @Test
    void constructor_withNullId_isValid() {
        BoardMember member = new BoardMember(null, boardId, userId, BoardRole.OWNER);

        assertThat(member.getId()).isNull();
    }

    @Test
    void constructor_withNullBoardId_throwsException() {
        assertThatThrownBy(() -> new BoardMember(memberId, null, userId, BoardRole.MEMBER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_withNullUserId_throwsException() {
        assertThatThrownBy(() -> new BoardMember(memberId, boardId, null, BoardRole.MEMBER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_withNullRole_throwsException() {
        assertThatThrownBy(() -> new BoardMember(memberId, boardId, userId, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
