package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.RenameUserRequest;
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
class RenameUserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RenameUserUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final User user = new User(userId, "Ana", "ana@test.com", "hash");

    @Test
    void execute_renamesAuthenticatedUserAndPublishesEvent() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User result = useCase.execute(new RenameUserRequest("Ana María"), userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("Ana María");

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publish(captor.capture());
        AuditDomainEvent event = (AuditDomainEvent) captor.getValue();
        assertThat(event.entityId()).isEqualTo(userId);
        assertThat(event.entityType()).isEqualTo("USER");
        assertThat(event.action()).isEqualTo("RENAMED");
        assertThat(event.boardId()).isNull();
        assertThat(event.performedBy()).isEqualTo(userId);
    }

    @Test
    void execute_withUnknownUser_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RenameUserRequest("Ana María"), userId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void execute_withBlankName_throwsAndSavesNothing() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(new RenameUserRequest(" "), userId))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(user.getName()).isEqualTo("Ana");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }
}
