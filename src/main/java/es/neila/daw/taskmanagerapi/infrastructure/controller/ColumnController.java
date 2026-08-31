package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.application.usecase.column.ChangeColumnOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.CreateColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.DeleteColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.RenameColumnUseCase;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/columns")
public class ColumnController {

    private final CreateColumnUseCase createColumnUseCase;
    private final RenameColumnUseCase renameColumnUseCase;
    private final ChangeColumnOrderUseCase changeColumnOrderUseCase;
    private final DeleteColumnUseCase deleteColumnUseCase;

    public ColumnController(CreateColumnUseCase createColumnUseCase, RenameColumnUseCase renameColumnUseCase, ChangeColumnOrderUseCase changeColumnOrderUseCase, DeleteColumnUseCase deleteColumnUseCase) {
        this.createColumnUseCase = createColumnUseCase;
        this.renameColumnUseCase = renameColumnUseCase;
        this.changeColumnOrderUseCase = changeColumnOrderUseCase;
        this.deleteColumnUseCase = deleteColumnUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Column create(@RequestBody CreateColumnRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return createColumnUseCase.execute(request, currentUserId);
    }

    @PutMapping("/rename")
    public Column rename(@RequestBody RenameColumnRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return renameColumnUseCase.execute(request, currentUserId);
    }

    @PutMapping("/order")
    public Column changeOrder(@RequestBody ChangeColumnOrderRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return changeColumnOrderUseCase.execute(request, currentUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteColumn(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteColumnUseCase.execute(id, userId);
    }
}
