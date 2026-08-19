package es.neila.daw.taskmanagerapi.application.usecase;

import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.UUID;

public class CreateColumnUseCase {

    private final ColumnRepository columnRepository;

    public CreateColumnUseCase(ColumnRepository columnRepository){
        this.columnRepository = columnRepository;
    }


    public Column execute(CreateColumnRequest request) {
        Column column = new Column(
                request.boardId(),
                UUID.randomUUID(),
                request.name(),
                request.order()
        );

        return columnRepository.save(column);
    }
}
