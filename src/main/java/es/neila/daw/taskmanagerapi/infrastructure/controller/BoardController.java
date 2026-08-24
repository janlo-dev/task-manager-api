package es.neila.daw.taskmanagerapi.infrastructure.controller;

import es.neila.daw.taskmanagerapi.application.dto.ChangeBoardOrderRequest;
import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.application.usecase.board.ChangeBoardOrderUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.CreateBoardUseCase;
import es.neila.daw.taskmanagerapi.application.usecase.board.RenameBoardUseCase;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final CreateBoardUseCase createBoardUseCase;
    private final RenameBoardUseCase renameBoardUseCase;
    private final ChangeBoardOrderUseCase changeBoardOrderUseCase;


    public BoardController(CreateBoardUseCase createBoardUseCase, RenameBoardUseCase renameBoardUseCase, ChangeBoardOrderUseCase changeBoardOrderUseCase) {
        this.createBoardUseCase = createBoardUseCase;
        this.renameBoardUseCase = renameBoardUseCase;
        this.changeBoardOrderUseCase = changeBoardOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Board create(@RequestBody CreateBoardRequest request) {
        return createBoardUseCase.execute(request);
    }

    @PutMapping("/rename")
    public Board rename(@RequestBody RenameBoardRequest request) {
        return renameBoardUseCase.execute(request);
    }

    @PutMapping("/order")
    public Board changeOrder(@RequestBody ChangeBoardOrderRequest request) {
        return changeBoardOrderUseCase.execute(request);
    }
}
