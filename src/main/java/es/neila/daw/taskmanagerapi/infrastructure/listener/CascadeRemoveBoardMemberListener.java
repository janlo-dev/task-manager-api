package es.neila.daw.taskmanagerapi.infrastructure.listener;

import es.neila.daw.taskmanagerapi.domain.event.BoardMemberRemovedEvent;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class CascadeRemoveBoardMemberListener {

    private final ColumnRepository columnRepository;
    private final TaskRepository taskRepository;

    public CascadeRemoveBoardMemberListener(ColumnRepository columnRepository, TaskRepository taskRepository) {
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
    }

    @EventListener
    @Transactional
    public void handleBoardMemberRemoved(BoardMemberRemovedEvent event) {
        // 1. Obtener todas las columnas del tablero
        List<UUID> columnIds = columnRepository.findIdsByBoardId(event.boardId());

        if (!columnIds.isEmpty()) {
            // 2. Desasignar las tareas de esas columnas asignadas al miembro expulsado
            taskRepository.unassignByColumnIdsAndUserId(columnIds, event.userId());
        }
    }
}
