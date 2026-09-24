package es.neila.daw.taskmanagerapi.application.usecase.task;

import es.neila.daw.taskmanagerapi.application.dto.AssignTaskRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
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
class AssignTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private AssignTaskUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();
    private final User assignee = new User(UUID.randomUUID(), "Carla", "carla@test.com", "hash");
    private final Column column = new Column(boardId, UUID.randomUUID(), "To do", 0);
    private final Task task = new Task(UUID.randomUUID(), "Título", "Desc", null, column.getId());
    private final AssignTaskRequest request = new AssignTaskRequest(task.getId(), assignee.getId());

    private void givenTaskInAccessibleColumn() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
    }

    @Test
    void execute_withBoardMember_assignsSavesAndPublishesEvent() {
        givenTaskInAccessibleColumn();
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, assignee.getId()))
                .thenReturn(Optional.of(new BoardMember(UUID.randomUUID(), boardId, assignee.getId(), BoardRole.MEMBER)));
        when(taskRepository.save(any(Task.class))).then(returnsFirstArg());

        Task result = useCase.execute(request, requesterId);

        assertThat(result.getAssignedUserId()).isEqualTo(assignee.getId());
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityType()).isEqualTo("TASK");
        assertThat(event.action()).isEqualTo("ASSIGNED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withUnknownTask_throws() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, requesterId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withoutAccess_throwsAndSavesNothing() {
        givenTaskInAccessibleColumn();
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, requesterId);

        assertThatThrownBy(() -> useCase.execute(request, requesterId))
                .isInstanceOf(UnauthorizedActionException.class);

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(userRepository, eventPublisher);
    }

    @Test
    void execute_withUnknownAssignee_throws() {
        givenTaskInAccessibleColumn();
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, requesterId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User to assign not found");

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withAssigneeNotInBoard_throwsAndDoesNotAssign() {
        givenTaskInAccessibleColumn();
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(boardMemberRepository.findByBoardIdAndUserId(boardId, assignee.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request, requesterId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not a member of this board");

        assertThat(task.getAssignedUserId()).isNull();
        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
