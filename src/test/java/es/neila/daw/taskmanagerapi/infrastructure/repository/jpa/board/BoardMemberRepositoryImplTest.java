package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.BoardMemberMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BoardMemberRepositoryImpl.class, BoardMemberMapper.class})
class BoardMemberRepositoryImplTest {

    @Autowired
    private BoardMemberRepositoryImpl memberRepository;

    @Autowired
    private TestEntityManager em;

    private final UUID boardA = UUID.randomUUID();
    private final UUID boardB = UUID.randomUUID();
    private final UUID ana = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();

    private BoardMember saveMember(UUID boardId, UUID userId, BoardRole role) {
        return memberRepository.save(new BoardMember(UUID.randomUUID(), boardId, userId, role));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindByBoardIdAndUserId_preservesAllFieldsIncludingRole() {
        BoardMember member = saveMember(boardA, ana, BoardRole.OWNER);
        flushAndClear();

        BoardMember found = memberRepository.findByBoardIdAndUserId(boardA, ana).orElseThrow();

        assertThat(found.getId()).isEqualTo(member.getId());
        assertThat(found.getBoardId()).isEqualTo(boardA);
        assertThat(found.getUserId()).isEqualTo(ana);
        assertThat(found.getRole()).isEqualTo(BoardRole.OWNER);
    }

    @Test
    void findByBoardIdAndUserId_whenNotMember_returnsEmpty() {
        saveMember(boardA, ana, BoardRole.OWNER);
        saveMember(boardB, carla, BoardRole.OWNER);
        flushAndClear();

        assertThat(memberRepository.findByBoardIdAndUserId(boardA, carla)).isEmpty();
    }

    @Test
    void findByBoardId_returnsOnlyMembersOfThatBoard() {
        saveMember(boardA, ana, BoardRole.OWNER);
        saveMember(boardA, carla, BoardRole.MEMBER);
        saveMember(boardB, ana, BoardRole.MEMBER);
        flushAndClear();

        assertThat(memberRepository.findByBoardId(boardA))
                .extracting(BoardMember::getUserId)
                .containsExactlyInAnyOrder(ana, carla);
    }

    @Test
    void findByUserId_returnsAllMembershipsOfThatUser() {
        saveMember(boardA, ana, BoardRole.OWNER);
        saveMember(boardB, ana, BoardRole.MEMBER);
        saveMember(boardA, carla, BoardRole.MEMBER);
        flushAndClear();

        assertThat(memberRepository.findByUserId(ana))
                .extracting(BoardMember::getBoardId)
                .containsExactlyInAnyOrder(boardA, boardB);
    }

    @Test
    void deleteByBoardIdAndUserId_deletesOnlyThatMembership() {
        saveMember(boardA, ana, BoardRole.OWNER);
        saveMember(boardA, carla, BoardRole.MEMBER);
        saveMember(boardB, carla, BoardRole.MEMBER);
        flushAndClear();

        memberRepository.deleteByBoardIdAndUserId(boardA, carla);
        flushAndClear();

        assertThat(memberRepository.findByBoardIdAndUserId(boardA, carla)).isEmpty();
        assertThat(memberRepository.findByBoardIdAndUserId(boardA, ana)).isPresent();
        assertThat(memberRepository.findByBoardIdAndUserId(boardB, carla)).isPresent();
    }

    @Test
    void deleteByBoardId_deletesOnlyMembershipsOfThatBoard() {
        saveMember(boardA, ana, BoardRole.OWNER);
        saveMember(boardA, carla, BoardRole.MEMBER);
        saveMember(boardB, ana, BoardRole.OWNER);
        flushAndClear();

        memberRepository.deleteByBoardId(boardA);
        flushAndClear();

        assertThat(memberRepository.findByBoardId(boardA)).isEmpty();
        assertThat(memberRepository.findByBoardId(boardB)).hasSize(1);
    }
}
