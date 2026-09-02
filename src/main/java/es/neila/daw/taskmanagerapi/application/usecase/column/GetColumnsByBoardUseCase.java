package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.List;
import java.util.UUID;

public class GetColumnsByBoardUseCase {

    private final ColumnRepository columnRepository;

    public GetColumnsByBoardUseCase(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public List<Column> execute(UUID boardId) {
        return columnRepository.findByBoardIdOrderByColumnOrderAsc(boardId);
    }
}