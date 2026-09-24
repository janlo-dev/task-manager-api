package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.ChangeUserEmailRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeUserEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ChangeUserEmailUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final User user = new User(userId, "Ana", "ana@test.com", "hash");

    @Test
    void execute_changesEmailOfAuthenticatedUserAndPublishesEvent() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("nueva@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User result = useCase.execute(new ChangeUserEmailRequest("nueva@test.com"), userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("nueva@test.com");

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(userId);
        assertThat(event.entityType()).isEqualTo("USER");
        assertThat(event.action()).isEqualTo("EMAIL_CHANGED");
        assertThat(event.boardId()).isNull();
    }

    @Test
    void execute_withOwnCurrentEmail_isAllowed() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User result = useCase.execute(new ChangeUserEmailRequest("ana@test.com"), userId);

        assertThat(result.getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    void execute_withEmailOfAnotherAccount_throwsAndSavesNothing() {
        User other = new User(UUID.randomUUID(), "Carla", "carla@test.com", "hash");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("carla@test.com")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> useCase.execute(new ChangeUserEmailRequest("carla@test.com"), userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");

        assertThat(user.getEmail()).isEqualTo("ana@test.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withUnknownUser_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ChangeUserEmailRequest("nueva@test.com"), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withBlankEmail_throwsAndSavesNothing() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(" ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ChangeUserEmailRequest(" "), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
