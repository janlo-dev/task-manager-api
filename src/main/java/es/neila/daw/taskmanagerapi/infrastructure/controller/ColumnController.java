package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.application.usecase.column.ChangeColumnOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.CreateColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.RenameColumnUseCase;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/columns")
public class ColumnController {

    private final CreateColumnUseCase createColumnUseCase;
    private final RenameColumnUseCase renameColumnUseCase;
    private final ChangeColumnOrderUseCase changeColumnOrderUseCase;

    public ColumnController(CreateColumnUseCase createColumnUseCase, RenameColumnUseCase renameColumnUseCase, ChangeColumnOrderUseCase changeColumnOrderUseCase) {
        this.createColumnUseCase = createColumnUseCase;
        this.renameColumnUseCase = renameColumnUseCase;
        this.changeColumnOrderUseCase = changeColumnOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Column create(@RequestBody CreateColumnRequest request) {
        return createColumnUseCase.execute(request);
    }

    @PutMapping("/rename")
    public Column rename(@RequestBody RenameColumnRequest request) {
        return renameColumnUseCase.execute(request);
    }

    @PutMapping("/order")
    public Column changeOrder(@RequestBody ChangeColumnOrderRequest request) {
        return changeColumnOrderUseCase.execute(request);
    }
}
