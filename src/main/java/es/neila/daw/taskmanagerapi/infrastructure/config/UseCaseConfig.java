package es.neila.daw.taskmanagerapi.infrastructure.config;

import es.neila.daw.taskmanagerapi.application.usecase.board.ChangeBoardOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.CreateBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.DeleteBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.RenameBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.ChangeColumnOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.CreateColumnUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.column.DeleteColumnUseCase;
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
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;

@Configuration
public class UseCaseConfig {

    // --- Task ---

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardRepository boardRepository) {
        return new CreateTaskUseCase(taskRepository, eventPublisher, columnRepository, boardRepository);
    }

    @Bean
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                           BoardRepository boardRepository) {
        return new MoveTaskUseCase(taskRepository, eventPublisher, columnRepository, boardRepository);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardRepository boardRepository) {
        return new DeleteTaskUseCase(taskRepository, eventPublisher, columnRepository, boardRepository);
    }

    @Bean
    public GetTasksByColumnUseCase getTasksByColumnUseCase(TaskRepository taskRepository) {
        return new GetTasksByColumnUseCase(taskRepository);
    }

    @Bean
    public RenameTaskUseCase renameTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardRepository boardRepository) {
        return new RenameTaskUseCase(taskRepository, eventPublisher, columnRepository, boardRepository);
    }

    @Bean
    public UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                                                     BoardRepository boardRepository) {
        return new UpdateTaskDescriptionUseCase(taskRepository, eventPublisher, columnRepository, boardRepository);
    }

    // --- Board ---

    @Bean
    public CreateBoardUseCase createBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        return new CreateBoardUseCase(boardRepository, eventPublisher);
    }

    @Bean
    public RenameBoardUseCase renameBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        return new RenameBoardUseCase(boardRepository, eventPublisher);
    }

    @Bean
    public ChangeBoardOrderUseCase changeBoardOrderUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        return new ChangeBoardOrderUseCase(boardRepository, eventPublisher);
    }

    @Bean
    public DeleteBoardUseCase deleteBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        return new DeleteBoardUseCase(boardRepository, eventPublisher);
    }

    // --- Column ---
    @Bean
    public CreateColumnUseCase createColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardRepository boardRepository) {
        return new CreateColumnUseCase(columnRepository, eventPublisher, boardRepository);
    }

    @Bean
    public RenameColumnUseCase renameColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardRepository boardRepository) {
        return new RenameColumnUseCase(columnRepository, eventPublisher, boardRepository);
    }

    @Bean
    public ChangeColumnOrderUseCase changeColumnOrderUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardRepository boardRepository) {
        return new ChangeColumnOrderUseCase(columnRepository, eventPublisher, boardRepository);
    }

    @Bean
    public DeleteColumnUseCase deleteColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardRepository boardRepository) {
        return new DeleteColumnUseCase(columnRepository, eventPublisher, boardRepository);
    }

    // --- User ---

    @Bean
    public RenameUserUseCase renameUserUseCase(UserRepository userRepository, DomainEventPublisher eventPublisher) {
        return new RenameUserUseCase(userRepository, eventPublisher);
    }

    @Bean
    public ChangeUserEmailUseCase changeUserEmailUseCase(UserRepository userRepository, DomainEventPublisher eventPublisher) {
        return new ChangeUserEmailUseCase(userRepository, eventPublisher);
    }
}
