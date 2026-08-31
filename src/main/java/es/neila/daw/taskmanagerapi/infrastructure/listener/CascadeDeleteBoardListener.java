package es.neila.daw.taskmanagerapi.infrastructure.listener;

import es.neila.daw.taskmanagerapi.domain.event.BoardDeletedEvent;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class CascadeDeleteBoardListener {

    private final ColumnRepository columnRepository;
    private final TaskRepository taskRepository;

    public CascadeDeleteBoardListener(ColumnRepository columnRepository, TaskRepository taskRepository) {
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
    }

    @EventListener
    @Transactional
    public void handleBoardDeleted(BoardDeletedEvent event) {
        // 1. Obtener todas las columnas pertenecientes al tablero eliminado
        List<UUID> columnIds = columnRepository.findIdsByBoardId(event.boardId());

        if (!columnIds.isEmpty()) {
            // 2. Borrar las tareas asociadas a esas columnas
            taskRepository.deleteByColumnIds(columnIds);

            // 3. Borrar las columnas
            columnRepository.deleteAllByIds(columnIds);
        }
    }
}
