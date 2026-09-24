package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.exception.UnauthorizedActionException;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetColumnsByBoardUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;
    @Mock
    private BoardAccessChecker boardAccessChecker;

    @InjectMocks
    private GetColumnsByBoardUseCase useCase;

    private final UUID boardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void execute_withAccess_returnsColumnsFromRepository() {
        List<Column> columns = List.of(
                new Column(boardId, UUID.randomUUID(), "To do", 0),
                new Column(boardId, UUID.randomUUID(), "Done", 1));
        when(columnRepository.findByBoardIdOrderByColumnOrderAsc(boardId)).thenReturn(columns);

        assertThat(useCase.execute(boardId, userId)).isEqualTo(columns);
        verify(boardAccessChecker).verifyCanEditContent(boardId, userId);
    }

    @Test
    void execute_withoutAccess_throwsAndQueriesNothing() {
        doThrow(new UnauthorizedActionException("no access"))
                .when(boardAccessChecker).verifyCanEditContent(boardId, userId);

        assertThatThrownBy(() -> useCase.execute(boardId, userId))
                .isInstanceOf(UnauthorizedActionException.class);

        verifyNoInteractions(columnRepository);
    }
}
