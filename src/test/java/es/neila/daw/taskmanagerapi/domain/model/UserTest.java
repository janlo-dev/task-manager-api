package es.neila.daw.taskmanagerapi.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private final UUID userId = UUID.randomUUID();

    private User newUser() {
        return new User(userId, "Ana", "ana@test.com", "hash");
    }

    @Test
    void constructor_withValidData_assignsFields() {
        User user = newUser();

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo("Ana");
        assertThat(user.getEmail()).isEqualTo("ana@test.com");
        assertThat(user.getPassword()).isEqualTo("hash");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void constructor_withBlankName_throwsException(String name) {
        assertThatThrownBy(() -> new User(userId, name, "ana@test.com", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void constructor_withBlankEmail_throwsException(String email) {
        assertThatThrownBy(() -> new User(userId, "Ana", email, "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void constructor_withBlankPassword_throwsException(String password) {
        assertThatThrownBy(() -> new User(userId, "Ana", "ana@test.com", password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rename_withValidName_changesName() {
        User user = newUser();

        user.rename("Carla");

        assertThat(user.getName()).isEqualTo("Carla");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void rename_withBlankName_throwsExceptionAndKeepsName(String newName) {
        User user = newUser();

        assertThatThrownBy(() -> user.rename(newName))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(user.getName()).isEqualTo("Ana");
    }

    @Test
    void changeEmail_withValidEmail_changesEmail() {
        User user = newUser();

        user.changeEmail("nueva@test.com");

        assertThat(user.getEmail()).isEqualTo("nueva@test.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void changeEmail_withBlankEmail_throwsExceptionAndKeepsEmail(String newEmail) {
        User user = newUser();

        assertThatThrownBy(() -> user.changeEmail(newEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User email cannot be empty");
        assertThat(user.getEmail()).isEqualTo("ana@test.com");
    }
}
