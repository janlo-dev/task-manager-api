package es.neila.daw.taskmanagerapi.infrastructure.mapper;

import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column.ColumnEntity;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {

    public ColumnEntity toEntity(Column column) {
        return new ColumnEntity(
                column.getId(),
                column.getBoardId(),
                column.getName(),
                column.getColumnOrder()
        );
    }

    public Column toDomain(ColumnEntity entity) {
        return new Column(
                entity.getBoardId(),
                entity.getId(),
                entity.getName(),
                entity.getColumnOrder()
        );
    }
}
