package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.BoardMemberDetailsResponse;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetBoardMembersUseCase {

    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final BoardAccessChecker boardAccessChecker;

    public GetBoardMembersUseCase(BoardMemberRepository boardMemberRepository, UserRepository userRepository, BoardAccessChecker boardAccessChecker) {
        this.boardMemberRepository = boardMemberRepository;
        this.userRepository = userRepository;
        this.boardAccessChecker = boardAccessChecker;
    }

    public List<BoardMemberDetailsResponse> execute(UUID boardId, UUID performedByUserId) {
        boardAccessChecker.verifyCanEditContent(boardId, performedByUserId);

        List<BoardMember> members = boardMemberRepository.findByBoardId(boardId);
        List<BoardMemberDetailsResponse> result = new ArrayList<>();

        for (BoardMember member : members) {
            // Miembro huérfano (usuario borrado): se omite para no tumbar el listado entero
            Optional<User> user = userRepository.findById(member.getUserId());
            if (user.isEmpty()) {
                continue;
            }

            result.add(new BoardMemberDetailsResponse(
                    member.getId(),
                    member.getUserId(),
                    user.get().getEmail(),
                    user.get().getName(),
                    member.getRole()
            ));
        }

        return result;
    }
}
