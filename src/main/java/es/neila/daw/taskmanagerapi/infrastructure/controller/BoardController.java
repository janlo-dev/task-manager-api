package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeBoardOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.application.usecase.board.*;
import es.neila.daw.taskmanagerapi.domain.model.Board;
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

    public BoardController(CreateBoardUseCase createBoardUseCase, RenameBoardUseCase renameBoardUseCase, ChangeBoardOrderUseCase changeBoardOrderUseCase, DeleteBoardUseCase deleteBoardUseCase, GetBoardsByUserUseCase getBoardsByUserUseCase) {
        this.createBoardUseCase = createBoardUseCase;
        this.renameBoardUseCase = renameBoardUseCase;
        this.changeBoardOrderUseCase = changeBoardOrderUseCase;
        this.deleteBoardUseCase = deleteBoardUseCase;
        this.getBoardsByUserUseCase = getBoardsByUserUseCase;
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
}
