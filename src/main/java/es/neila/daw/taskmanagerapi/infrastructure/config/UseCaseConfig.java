package es.neila.daw.taskmanagerapi.infrastructure.config;

import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.application.usecase.audit.GetAuditLogByEntityUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.audit.GetAuditLogByUserUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.*;
import es.neila.daw.taskmanagerapi.application.usecase.column.*;
import es.neila.daw.taskmanagerapi.application.usecase.task.*;
import es.neila.daw.taskmanagerapi.application.usecase.user.ChangeUserEmailUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.task.GetUserAssignedTasksUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.user.RenameUserUseCase;
import es.neila.daw.taskmanagerapi.domain.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;

@Configuration
public class UseCaseConfig {

    // --- Task ---
    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardAccessChecker boardAccessChecker) {
        return new CreateTaskUseCase(taskRepository, eventPublisher, columnRepository, boardAccessChecker);
    }

    @Bean
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                           BoardAccessChecker boardAccessChecker) {
        return new MoveTaskUseCase(taskRepository, eventPublisher, columnRepository, boardAccessChecker);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardAccessChecker boardAccessChecker) {
        return new DeleteTaskUseCase(taskRepository, eventPublisher, columnRepository, boardAccessChecker);
    }

    @Bean
    public GetTasksByColumnUseCase getTasksByColumnUseCase(TaskRepository taskRepository) {
        return new GetTasksByColumnUseCase(taskRepository);
    }

    @Bean
    public RenameTaskUseCase renameTaskUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                               BoardAccessChecker boardAccessChecker) {
        return new RenameTaskUseCase(taskRepository, eventPublisher, columnRepository, boardAccessChecker);
    }

    @Bean
    public UpdateTaskDescriptionUseCase updateTaskDescriptionUseCase(TaskRepository taskRepository, DomainEventPublisher eventPublisher, ColumnRepository columnRepository,
                                                                     BoardAccessChecker boardAccessChecker) {
        return new UpdateTaskDescriptionUseCase(taskRepository, eventPublisher, columnRepository, boardAccessChecker);
    }

    @Bean
    public AssignTaskUseCase assignTaskUseCase(TaskRepository taskRepository, ColumnRepository columnRepository, BoardAccessChecker boardAccessChecker, UserRepository userRepository, DomainEventPublisher eventPublisher) {
        return new AssignTaskUseCase(taskRepository, columnRepository, boardAccessChecker, userRepository, eventPublisher);
    }

    // --- Board ---
    @Bean
    public CreateBoardUseCase createBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher, BoardMemberRepository boardMemberRepository) {
        return new CreateBoardUseCase(boardRepository, eventPublisher, boardMemberRepository);
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

    @Bean
    public GetBoardsByUserUseCase getBoardsByUserUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository) {
        return new GetBoardsByUserUseCase(boardRepository, boardMemberRepository);
    }

    @Bean
    public BoardAccessChecker boardAccessChecker(BoardMemberRepository boardMemberRepository) {
        return new BoardAccessChecker(boardMemberRepository);
    }

    @Bean
    public InviteBoardMemberUseCase inviteBoardMemberUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository, UserRepository userRepository, DomainEventPublisher eventPublisher) {
        return new InviteBoardMemberUseCase(boardRepository, boardMemberRepository, userRepository, eventPublisher);
    }

    @Bean
    public RemoveBoardMemberUseCase removeBoardMemberUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository, DomainEventPublisher eventPublisher) {
        return new RemoveBoardMemberUseCase(boardRepository, boardMemberRepository, eventPublisher);
    }

    @Bean
    public GetBoardMembersUseCase getBoardMembersUseCase(BoardMemberRepository boardMemberRepository) {
        return new GetBoardMembersUseCase(boardMemberRepository);
    }


    // --- Column ---
    @Bean
    public CreateColumnUseCase createColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardAccessChecker boardAccessChecker) {
        return new CreateColumnUseCase(columnRepository, eventPublisher, boardAccessChecker);
    }

    @Bean
    public RenameColumnUseCase renameColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardAccessChecker boardAccessChecker) {
        return new RenameColumnUseCase(columnRepository, eventPublisher, boardAccessChecker);
    }

    @Bean
    public ChangeColumnOrderUseCase changeColumnOrderUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardAccessChecker boardAccessChecker) {
        return new ChangeColumnOrderUseCase(columnRepository, eventPublisher, boardAccessChecker);
    }

    @Bean
    public DeleteColumnUseCase deleteColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardAccessChecker boardAccessChecker) {
        return new DeleteColumnUseCase(columnRepository, eventPublisher, boardAccessChecker);
    }

    @Bean
    public GetColumnsByBoardUseCase getColumnsByBoardUseCase(ColumnRepository columnRepository) {
        return new GetColumnsByBoardUseCase(columnRepository);
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

    @Bean
    public GetUserAssignedTasksUseCase getUserAssignedTasksUseCase(TaskRepository taskRepository, ColumnRepository columnRepository, BoardRepository boardRepository) {
        return new GetUserAssignedTasksUseCase(taskRepository, columnRepository, boardRepository);
    }

    // --- Audit ---
    @Bean
    public GetAuditLogByEntityUseCase getAuditLogByEntityUseCase(AuditLogRepository auditLogRepository) {
        return new GetAuditLogByEntityUseCase(auditLogRepository);
    }

    @Bean
    public GetAuditLogByUserUseCase getAuditLogByUserUseCase(AuditLogRepository auditLogRepository) {
        return new GetAuditLogByUserUseCase(auditLogRepository);
    }
}
