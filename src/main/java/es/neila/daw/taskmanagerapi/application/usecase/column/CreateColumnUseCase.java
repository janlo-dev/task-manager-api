package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.CreateColumnRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.UUID;


public class CreateColumnUseCase {

    private final ColumnRepository columnRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher){
        this.columnRepository = columnRepository;
        this.eventPublisher = eventPublisher;
    }


    public Column execute(CreateColumnRequest request, UUID performedByUserId) {
        Column column = new Column(
                request.boardId(),
                UUID.randomUUID(),
                request.name(),
                request.columnOrder()
        );

        Column saveColumn =  columnRepository.save(column);

        eventPublisher.publish(new AuditDomainEvent(
                saveColumn.getId(),
                "COLUMN",
                "CREATED",
                performedByUserId,
                "Column created with name: " + saveColumn.getName()
        ));

        return saveColumn;
    }
}
