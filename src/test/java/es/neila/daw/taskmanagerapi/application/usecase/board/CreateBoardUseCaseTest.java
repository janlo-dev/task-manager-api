package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBoardUseCaseTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private CreateBoardUseCase useCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_savesBoardOwnerMembershipAndPublishesEvent() {
        when(boardRepository.save(any(Board.class))).then(returnsFirstArg());

        Board result = useCase.execute(new CreateBoardRequest("Tablero", 0), userId);

        assertThat(result.getName()).isEqualTo("Tablero");
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBoardOrder()).isZero();

        ArgumentCaptor<BoardMember> memberCaptor = ArgumentCaptor.forClass(BoardMember.class);
        verify(boardMemberRepository).save(memberCaptor.capture());
        BoardMember owner = memberCaptor.getValue();
        assertThat(owner.getBoardId()).isEqualTo(result.getId());
        assertThat(owner.getUserId()).isEqualTo(userId);
        assertThat(owner.getRole()).isEqualTo(BoardRole.OWNER);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        AuditDomainEvent event = (AuditDomainEvent) eventCaptor.getValue();
        assertThat(event.entityId()).isEqualTo(result.getId());
        assertThat(event.entityType()).isEqualTo("BOARD");
        assertThat(event.action()).isEqualTo("CREATED");
        assertThat(event.boardId()).isEqualTo(result.getId());
        assertThat(event.performedBy()).isEqualTo(userId);
    }

    @Test
    void execute_withBlankName_throwsAndSavesNothing() {
        assertThatThrownBy(() -> useCase.execute(new CreateBoardRequest(" ", 0), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(boardRepository, boardMemberRepository, eventPublisher);
    }
}
