package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.user;

import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({UserRepositoryImpl.class, UserMapper.class})
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager em;

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_andFindById_preservesAllFields() {
        User user = userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        flushAndClear();

        User found = userRepository.findById(user.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Ana");
        assertThat(found.getEmail()).isEqualTo("ana@test.com");
        assertThat(found.getPassword()).isEqualTo("hash");
    }

    @Test
    void findByEmail_returnsMatchingUser() {
        User ana = userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        userRepository.save(new User(UUID.randomUUID(), "Carla", "carla@test.com", "hash"));
        flushAndClear();

        assertThat(userRepository.findByEmail("ana@test.com"))
                .get()
                .extracting(User::getId)
                .isEqualTo(ana.getId());
    }

    @Test
    void findByEmail_withUnknownEmail_returnsEmpty() {
        userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        flushAndClear();

        assertThat(userRepository.findByEmail("nadie@test.com")).isEmpty();
    }

    @Test
    void save_withDuplicateEmail_isRejectedByDatabase() {
        userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        userRepository.save(new User(UUID.randomUUID(), "Otra Ana", "ana@test.com", "hash"));

        // El INSERT llega a la BD al hacer flush; el proxy de Spring Data traduce la excepción
        assertThatThrownBy(() -> jpaRepository.flush())
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_existingUserWithSameEmail_updatesWithoutViolatingUniqueIndex() {
        User user = userRepository.save(new User(UUID.randomUUID(), "Ana", "ana@test.com", "hash"));
        flushAndClear();

        User loaded = userRepository.findById(user.getId()).orElseThrow();
        loaded.rename("Ana María");
        userRepository.save(loaded);
        flushAndClear();

        assertThat(userRepository.findByEmail("ana@test.com").orElseThrow().getName()).isEqualTo("Ana María");
    }
}
