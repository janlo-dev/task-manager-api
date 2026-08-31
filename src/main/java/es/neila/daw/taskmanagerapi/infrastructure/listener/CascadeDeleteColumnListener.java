package es.neila.daw.taskmanagerapi.infrastructure.listener;

import es.neila.daw.taskmanagerapi.domain.event.ColumnDeletedEvent;
import es.neila.daw.taskmanagerapi.domain.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CascadeDeleteColumnListener {

    private final TaskRepository taskRepository;

    public CascadeDeleteColumnListener(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @EventListener
    @Transactional
    public void handleColumnDeleted(ColumnDeletedEvent event) {
        // Borra todas las tareas de esa columna
        taskRepository.deleteByColumnIds(List.of(event.columnId()));
    }
}
