package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.List;
import java.util.UUID;

public class GetColumnsByBoardUseCase {

    private final ColumnRepository columnRepository;
    private final BoardAccessChecker boardAccessChecker;

    public GetColumnsByBoardUseCase(ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker) {
        this.columnRepository = columnRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public List<Column> execute(UUID boardId, UUID performedByUserId) {
        boardAccessChecker.verifyCanEditContent(boardId, performedByUserId);

        return columnRepository.findByBoardIdOrderByColumnOrderAsc(boardId);
    }
}
