package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeBoardOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.application.dto.InviteBoardMemberRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.application.usecase.board.*;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final CreateBoardUseCase createBoardUseCase;
    private final RenameBoardUseCase renameBoardUseCase;
    private final ChangeBoardOrderUseCase changeBoardOrderUseCase;
    private final DeleteBoardUseCase deleteBoardUseCase;
    private final GetBoardsByUserUseCase getBoardsByUserUseCase;
    private final InviteBoardMemberUseCase inviteBoardMemberUseCase;
    private final RemoveBoardMemberUseCase removeBoardMemberUseCase;
    private final GetBoardMembersUseCase getBoardMembersUseCase;

    public BoardController(CreateBoardUseCase createBoardUseCase, RenameBoardUseCase renameBoardUseCase, ChangeBoardOrderUseCase changeBoardOrderUseCase, DeleteBoardUseCase deleteBoardUseCase, GetBoardsByUserUseCase getBoardsByUserUseCase, InviteBoardMemberUseCase inviteBoardMemberUseCase, RemoveBoardMemberUseCase removeBoardMemberUseCase, GetBoardMembersUseCase getBoardMembersUseCase) {
        this.createBoardUseCase = createBoardUseCase;
        this.renameBoardUseCase = renameBoardUseCase;
        this.changeBoardOrderUseCase = changeBoardOrderUseCase;
        this.deleteBoardUseCase = deleteBoardUseCase;
        this.getBoardsByUserUseCase = getBoardsByUserUseCase;
        this.inviteBoardMemberUseCase = inviteBoardMemberUseCase;
        this.removeBoardMemberUseCase = removeBoardMemberUseCase;
        this.getBoardMembersUseCase = getBoardMembersUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Board create(@RequestBody CreateBoardRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return createBoardUseCase.execute(request, currentUserId);
    }

    @PutMapping("/rename")
    public Board rename(@RequestBody RenameBoardRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return renameBoardUseCase.execute(request, currentUserId);
    }

    @PutMapping("/order")
    public Board changeOrder(@RequestBody ChangeBoardOrderRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());

        return changeBoardOrderUseCase.execute(request, currentUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoard(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        deleteBoardUseCase.execute(id, userId);
    }

    @GetMapping("/me")
    public List<Board> getMyBoards(Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return getBoardsByUserUseCase.execute(currentUserId);
    }

    // --- Miembros del tablero ---

    @PostMapping("/members/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardMember inviteMember(@RequestBody InviteBoardMemberRequest request, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        return inviteBoardMemberUseCase.execute(request, currentUserId);
    }

    @DeleteMapping("/{boardId}/members/{memberUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID boardId, @PathVariable UUID memberUserId, Authentication authentication) {
        UUID currentUserId = UUID.fromString(authentication.getName());
        removeBoardMemberUseCase.execute(boardId, memberUserId, currentUserId);
    }

    @GetMapping("/{boardId}/members")
    public List<BoardMember> getMembers(@PathVariable UUID boardId) {
        return getBoardMembersUseCase.execute(boardId);
    }
}
