package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.application.usecase.column.*;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/columns")
public class ColumnController {

    private final CreateColumnUseCase createColumnUseCase;
    private final RenameColumnUseCase renameColumnUseCase;
    private final ChangeColumnOrderUseCase changeColumnOrderUseCase;
    private final DeleteColumnUseCase deleteColumnUseCase;
    private final GetColumnsByBoardUseCase getColumnsByBoardUseCase;

    public ColumnController(CreateColumnUseCase createColumnUseCase, RenameColumnUseCase renameColumnUseCase, ChangeColumnOrderUseCase changeColumnOrderUseCase, DeleteColumnUseCase deleteColumnUseCase, GetColumnsByBoardUseCase getColumnsByBoardUseCase) {
        this.createColumnUseCase = createColumnUseCase;
        this.renameColumnUseCase = renameColumnUseCase;
        this.changeColumnOrderUseCase = changeColumnOrderUseCase;
        this.deleteColumnUseCase = deleteColumnUseCase;
        this.getColumnsByBoardUseCase = getColumnsByBoardUseCase;
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

    @GetMapping("/board/{boardId}")
    public List<Column> getByBoard(@PathVariable UUID boardId) {
        return getColumnsByBoardUseCase.execute(boardId);
    }
}
