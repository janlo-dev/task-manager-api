package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
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
class CreateColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private CreateColumnUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_withAccess_savesColumnAndPublishesEvent() {
        when(columnRepository.save(any(Column.class))).then(returnsFirstArg());

        Column result = useCase.execute(new CreateColumnRequest("To do", 0, boardId), userId);

        assertThat(result.getBoardId()).isEqualTo(boardId);
        assertThat(result.getName()).isEqualTo("To do");
        assertThat(result.getColumnOrder()).isZero();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(result.getId());
        assertThat(event.entityType()).isEqualTo("COLUMN");
        assertThat(event.action()).isEqualTo("CREATED");
        assertThat(event.boardId()).isEqualTo(boardId);
    }

    @Test
    void execute_withoutAccess_throwsAndSavesNothing() {
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(new CreateColumnRequest("To do", 0, boardId), userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(columnRepository, eventPublisher);
    }

    @Test
    void execute_withBlankName_throwsAndSavesNothing() {
        assertThatThrownBy(() -> useCase.execute(new CreateColumnRequest(" ", 0, boardId), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(columnRepository, eventPublisher);
    }

    @Test
    void execute_withNegativeOrder_throwsAndSavesNothing() {
        assertThatThrownBy(() -> useCase.execute(new CreateColumnRequest("To do", -1, boardId), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(columnRepository, eventPublisher);
    }
}
