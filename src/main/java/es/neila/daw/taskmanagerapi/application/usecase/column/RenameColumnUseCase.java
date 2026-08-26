package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.RenameColumnRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.UUID;


public class RenameColumnUseCase {

    private final ColumnRepository columnRepository;
    private final DomainEventPublisher eventPublisher;


    public RenameColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher) {
        this.columnRepository = columnRepository;
        this.eventPublisher = eventPublisher;
    }

    public Column execute(RenameColumnRequest request, UUID performedByUserId) {
        Column column = columnRepository.findById(request.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        column.rename(request.newName());

        Column updateColumn =  columnRepository.save(column);

        eventPublisher.publish(new AuditDomainEvent(
                updateColumn.getId(),
                "COLUMN",
                "RENAMED",
                performedByUserId,
                "Column update with name: " + updateColumn.getName()
        ));

        return updateColumn;
    }


}
