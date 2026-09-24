package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.user;

import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({UserRepositoryImpl.class, UserMapper.class})
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserJpaRepository jpaRepository;

    @Test
    void save_withDuplicateEmail_isRejectedByDatabase() {
        userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        userRepository.save(new User(UUID.randomUUID(), "Otra Ana", "ana@test.com", "hash"));

        // El INSERT llega a la BD al hacer flush; el proxy de Spring Data traduce la excepción
        assertThatThrownBy(() -> jpaRepository.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
