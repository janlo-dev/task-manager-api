package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.CreateTaskRequest;
import es.neila.daw.taskmanagerapi.application.dto.MoveTaskRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameTaskRequest;
import es.neila.daw.taskmanagerapi.application.dto.UpdateTaskDescriptionRequest;
import es.neila.daw.taskmanagerapi.application.usecase.task.*;
import es.neila.daw.taskmanagerapi.domain.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final RenameTaskUseCase renameTaskUseCase;
    private final UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase;
    private final MoveTaskUseCase moveTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final GetTasksByColumnUseCase getTasksByColumnUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase, RenameTaskUseCase renameTaskUseCase, UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase, MoveTaskUseCase moveTaskUseCase, DeleteTaskUseCase deleteTaskUseCase, GetTasksByColumnUseCase getTasksByColumnUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.renameTaskUseCase = renameTaskUseCase;
        this.updateTaskDescriptionUseCase = updateTaskDescriptionUseCase;
        this.moveTaskUseCase = moveTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.getTasksByColumnUseCase = getTasksByColumnUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody CreateTaskRequest request) {
        return createTaskUseCase.execute(request);
    }

    @GetMapping
    public List<Task> getByColumn(@RequestParam UUID columnId) {
        return getTasksByColumnUseCase.execute(columnId);
    }

    @PutMapping("/rename")
    public Task rename(@RequestBody RenameTaskRequest request) {
        return renameTaskUseCase.execute(request);
    }

    @PutMapping("/description")
    public Task updateDescription(@RequestBody UpdateTaskDescriptionRequest request) {
        return updateTaskDescriptionUseCase.execute(request);
    }

    @PutMapping("/move")
    public Task move(@RequestBody MoveTaskRequest request) {
        return moveTaskUseCase.execute(request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID taskId) {
        deleteTaskUseCase.execute(taskId);
    }
}
