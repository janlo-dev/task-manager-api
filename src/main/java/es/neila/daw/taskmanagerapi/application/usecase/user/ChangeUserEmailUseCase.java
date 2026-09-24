package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.ChangeUserEmailRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.UUID;

public class ChangeUserEmailUseCase {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public ChangeUserEmailUseCase(UserRepository userRepository, DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    // Solo se puede cambiar el email del usuario autenticado
    public User execute(ChangeUserEmailRequest request, UUID performedByUserId) {
        User user = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.findByEmail(request.newEmail())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already in use");
                });

        user.changeEmail(request.newEmail());

        User updateUser =  userRepository.save(user);

        eventPublisher.publish(new AuditDomainEvent(
                updateUser.getId(),
                "USER",
                null,
                "EMAIL_CHANGED",
                performedByUserId,
                "User email changed to: " + updateUser.getEmail()
        ));

        return updateUser;
    }
}
