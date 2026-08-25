package es.neila.daw.taskmanagerapi.infrastructure.config;

import es.neila.daw.taskmanagerapi.application.usecase.board.ChangeBoardOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.CreateBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.RenameBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.ChangeColumnOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.CreateColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.RenameColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.task.*;
import es.neila.daw.taskmanagerapi.application.usecase.user.ChangeUserEmailUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.user.RenameUserUseCase;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    // --- Task ---

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository) {
        return new CreateTaskUseCase(taskRepository);
    }

    @Bean
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository) {
        return new MoveTaskUseCase(taskRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository) {
        return new DeleteTaskUseCase(taskRepository);
    }

    @Bean
    public GetTasksByColumnUseCase getTasksByColumnUseCase(TaskRepository taskRepository) {
        return new GetTasksByColumnUseCase(taskRepository);
    }

    @Bean
    public RenameTaskUseCase renameTaskUseCase(TaskRepository taskRepository) {
        return new RenameTaskUseCase(taskRepository);
    }

    @Bean
    public UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase(TaskRepository taskRepository) {
        return new UpdateTaskDescriptionUseCase(taskRepository);
    }

    // --- Board ---

    @Bean
    public CreateBoardUseCase createBoardUseCase(BoardRepository boardRepository) {
        return new CreateBoardUseCase(boardRepository);
    }

    @Bean
    public RenameBoardUseCase renameBoardUseCase(BoardRepository boardRepository) {
        return new RenameBoardUseCase(boardRepository);
    }

    @Bean
    public ChangeBoardOrderUseCase changeBoardOrderUseCase(BoardRepository boardRepository) {
        return new ChangeBoardOrderUseCase(boardRepository);
    }

    // --- Column ---
    @Bean
    public CreateColumnUseCase createColumnUseCase(ColumnRepository columnRepository) {
        return new CreateColumnUseCase(columnRepository);
    }

    @Bean
    public RenameColumnUseCase renameColumnUseCase(ColumnRepository columnRepository) {
        return new RenameColumnUseCase(columnRepository);
    }

    @Bean
    public ChangeColumnOrderUseCase changeColumnOrderUseCase(ColumnRepository columnRepository) {
        return new ChangeColumnOrderUseCase(columnRepository);
    }

    // --- User ---

    @Bean
    public RenameUserUseCase renameUserUseCase(UserRepository userRepository) {
        return new RenameUserUseCase(userRepository);
    }

    @Bean
    public ChangeUserEmailUseCase changeUserEmailUseCase(UserRepository userRepository) {
        return new ChangeUserEmailUseCase(userRepository);
    }

}
