package es.neila.daw.taskmanagerapi.application.usecase;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

public class ChangeColumnOrderUseCase {

    private final ColumnRepository columnRepository;

    public ChangeColumnOrderUseCase(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public Column execute(ChangeColumnOrderRequest request) {
        Column column = columnRepository.findById(request.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        column.changeOrder(request.newOrder());

        return columnRepository.save(column);
    }
}
