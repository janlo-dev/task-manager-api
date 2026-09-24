package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column;

import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.ColumnMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ColumnRepositoryImpl.class, ColumnMapper.class})
class ColumnRepositoryImplTest {

    @Autowired
    private ColumnRepositoryImpl columnRepository;

    @Autowired
    private TestEntityManager em;

    private final UUID boardA = UUID.randomUUID();
    private final UUID boardB = UUID.randomUUID();

    private Column saveColumn(UUID boardId, String name, int order) {
        return columnRepository.save(new Column(boardId, UUID.randomUUID(), name, order));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindById_preservesAllFields() {
        Column column = saveColumn(boardA, "To do", 2);
        flushAndClear();

        Column found = columnRepository.findById(column.getId()).orElseThrow();

        assertThat(found.getBoardId()).isEqualTo(boardA);
        assertThat(found.getName()).isEqualTo("To do");
        assertThat(found.getColumnOrder()).isEqualTo(2);
    }

    @Test
    void findByBoardIdOrderByColumnOrderAsc_returnsOnlyThatBoardInOrder() {
        saveColumn(boardA, "Done", 2);
        saveColumn(boardA, "To do", 0);
        saveColumn(boardB, "Otro board", 1);
        saveColumn(boardA, "Doing", 1);
        flushAndClear();

        List<Column> result = columnRepository.findByBoardIdOrderByColumnOrderAsc(boardA);

        assertThat(result).extracting(Column::getName).containsExactly("To do", "Doing", "Done");
    }

    @Test
    void findIdsByBoardId_returnsOnlyIdsOfThatBoard() {
        Column a1 = saveColumn(boardA, "A1", 0);
        Column a2 = saveColumn(boardA, "A2", 1);
        saveColumn(boardB, "B1", 0);
        flushAndClear();

        assertThat(columnRepository.findIdsByBoardId(boardA)).containsExactlyInAnyOrder(a1.getId(), a2.getId());
    }

    @Test
    void findIdsByBoardId_withoutColumns_returnsEmptyList() {
        assertThat(columnRepository.findIdsByBoardId(UUID.randomUUID())).isEmpty();
    }

    @Test
    void deleteAllByIds_deletesOnlyThoseColumns() {
        Column a1 = saveColumn(boardA, "A1", 0);
        Column a2 = saveColumn(boardA, "A2", 1);
        Column b1 = saveColumn(boardB, "B1", 0);
        flushAndClear();

        columnRepository.deleteAllByIds(List.of(a1.getId(), a2.getId()));
        flushAndClear();

        assertThat(columnRepository.findById(a1.getId())).isEmpty();
        assertThat(columnRepository.findById(a2.getId())).isEmpty();
        assertThat(columnRepository.findById(b1.getId())).isPresent();
    }

    @Test
    void save_existingColumn_updatesInsteadOfDuplicating() {
        Column column = saveColumn(boardA, "To do", 0);
        flushAndClear();

        Column loaded = columnRepository.findById(column.getId()).orElseThrow();
        loaded.rename("Pendiente");
        loaded.changeOrder(3);
        columnRepository.save(loaded);
        flushAndClear();

        assertThat(columnRepository.findByBoardIdOrderByColumnOrderAsc(boardA))
                .singleElement()
                .satisfies(c -> {
                    assertThat(c.getName()).isEqualTo("Pendiente");
                    assertThat(c.getColumnOrder()).isEqualTo(3);
                });
    }

    @Test
    void delete_removesOnlyThatColumn() {
        Column toDelete = saveColumn(boardA, "A1", 0);
        Column toKeep = saveColumn(boardA, "A2", 1);
        flushAndClear();

        columnRepository.delete(toDelete.getId());
        flushAndClear();

        assertThat(columnRepository.findById(toDelete.getId())).isEmpty();
        assertThat(columnRepository.findById(toKeep.getId())).isPresent();
    }
}
