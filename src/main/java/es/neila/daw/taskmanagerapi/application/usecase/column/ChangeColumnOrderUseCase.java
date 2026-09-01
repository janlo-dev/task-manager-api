package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.UUID;


public class ChangeColumnOrderUseCase {

    private final ColumnRepository columnRepository;
    private final DomainEventPublisher eventPublisher;
    private final BoardAccessChecker boardAccessChecker;

    public ChangeColumnOrderUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardAccessChecker boardAccessChecker) {
        this.columnRepository = columnRepository;
        this.eventPublisher = eventPublisher;
        this.boardAccessChecker = boardAccessChecker;
    }

    public Column execute(ChangeColumnOrderRequest request, UUID performedByUserId) {

        Column column = columnRepository.findById(request.columnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        boardAccessChecker.verifyCanEditContent(column.getBoardId(), performedByUserId);

        column.changeOrder(request.newOrder());

        Column updateColumn = columnRepository.save(column);

        eventPublisher.publish(new AuditDomainEvent(
                updateColumn.getId(),
                "COLUMN",
                "REORDERED",
                performedByUserId,
                "Column order change to: " + updateColumn.getColumnOrder()
        ));

        return updateColumn;
    }
}
