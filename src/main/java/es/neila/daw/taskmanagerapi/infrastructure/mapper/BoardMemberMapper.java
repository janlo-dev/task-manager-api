package es.neila.daw.taskmanagerapi.infrastructure.mapper;

import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board.BoardMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardMemberMapper {

    public BoardMemberEntity toEntity(BoardMember boardMember) {
        return new BoardMemberEntity(
                boardMember.getId(),
                boardMember.getBoardId(),
                boardMember.getUserId(),
                boardMember.getRole()
        );
    }

    public BoardMember toDomain(BoardMemberEntity entity) {
        return new BoardMember(
                entity.getId(),
                entity.getBoardId(),
                entity.getUserId(),
                entity.getRole()
        );
    }
}
