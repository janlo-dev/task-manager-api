package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeUserEmailRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameUserRequest;
import es.neila.daw.taskmanagerapi.application.dto.UserTaskProjectionResponse;
import es.neila.daw.taskmanagerapi.application.usecase.task.GetUserAssignedTasksUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.user.ChangeUserEmailUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.user.RenameUserUseCase;
import es.neila.daw.taskmanagerapi.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RenameUserUseCase renameUserUseCase;
    private final ChangeUserEmailUseCase changeUserEmailUseCase;
    private final GetUserAssignedTasksUseCase getUserAssignedTasksUseCase;

    public UserController(RenameUserUseCase renameUserUseCase, ChangeUserEmailUseCase changeUserEmailUseCase, GetUserAssignedTasksUseCase getUserAssignedTasksUseCase) {
        this.renameUserUseCase = renameUserUseCase;
        this.changeUserEmailUseCase = changeUserEmailUseCase;
        this.getUserAssignedTasksUseCase = getUserAssignedTasksUseCase;
    }

    @PutMapping("/rename")
    public User rename(@RequestBody RenameUserRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return renameUserUseCase.execute(request, currentUserId);
    }

    @PutMapping("/email")
    public User changeEmail(@RequestBody ChangeUserEmailRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return changeUserEmailUseCase.execute(request, currentUserId);
    }

    @GetMapping("/me")
    public ResponseEntity<List<UserTaskProjectionResponse>> getUserAssignedTasks(Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        List<UserTaskProjectionResponse> tasks = getUserAssignedTasksUseCase.execute(currentUserId);
        return ResponseEntity.ok(tasks);
    }
}
