package es.neila.daw.taskmanagerapi.application.usecase;

import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

public class RenameColumnUseCase {

    private final ColumnRepository columnRepository;


    public RenameColumnUseCase(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public Column execute(RenameColumnRequest request) {
        Column column = columnRepository.findById(request.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        column.rename(request.newName());

        return columnRepository.save(column);
    }


}
