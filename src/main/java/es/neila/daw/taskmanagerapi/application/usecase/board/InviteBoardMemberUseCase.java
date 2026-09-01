package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.InviteBoardMemberRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.UUID;

public class InviteBoardMemberUseCase {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public InviteBoardMemberUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository, UserRepository userRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.boardMemberRepository = boardMemberRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public BoardMember execute(InviteBoardMemberRequest request, UUID performedBy) {
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.verifyCanManage(performedBy);   // solo el OWNER puede invitar

        User invitedUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No user found with that email"));

        boardMemberRepository.findByBoardIdAndUserId(request.boardId(), invitedUser.getId())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("User is already a member of this board");
                });

        BoardMember member = new BoardMember(
                UUID.randomUUID(),
                request.boardId(),
                invitedUser.getId(),
                BoardRole.MEMBER
        );
        BoardMember savedMember = boardMemberRepository.save(member);

        eventPublisher.publish(new AuditDomainEvent(
                request.boardId(),
                "BOARD",
                "MEMBER_ADDED",
                performedBy,
                "User invited to board: " + invitedUser.getEmail()
        ));

        return savedMember;
    }
}
