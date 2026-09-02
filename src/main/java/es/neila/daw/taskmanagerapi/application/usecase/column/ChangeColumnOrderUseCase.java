package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.application.dto.ChangeColumnOrderRequest;
import es.neila.daw.taskmanagerapi.application.service.BoardAccessChecker;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.List;
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

        int oldOrder = column.getColumnOrder();
        int newOrder = request.newOrder();

        if (oldOrder != newOrder) {
            List<Column> siblings = columnRepository.findByBoardIdOrderByColumnOrderAsc(column.getBoardId());

            for (Column sibling : siblings) {
                if (sibling.getId().equals(column.getId())) {
                    continue; // la propia columna se actualiza aparte, más abajo
                }

                int siblingOrder = sibling.getColumnOrder();

                if (oldOrder < newOrder) {
                    // se movió hacia adelante -> las de en medio retroceden
                    if (siblingOrder > oldOrder && siblingOrder <= newOrder) {
                        sibling.changeOrder(siblingOrder - 1);
                        columnRepository.save(sibling);
                    }
                } else {
                    // se movió hacia atrás -> las de en medio avanzan
                    if (siblingOrder >= newOrder && siblingOrder < oldOrder) {
                        sibling.changeOrder(siblingOrder + 1);
                        columnRepository.save(sibling);
                    }
                }
            }
        }


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
