package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.ChangeUserEmailRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

public class ChangeUserEmailUseCase {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public ChangeUserEmailUseCase(UserRepository userRepository, DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public User execute(ChangeUserEmailRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.changeEmail(request.newEmail());

        User updateUser =  userRepository.save(user);

        eventPublisher.publish(new AuditDomainEvent(
                updateUser.getId(),
                "USER",
                "EMAIL_CHANGED",
                null,
                "User email changed to: " + updateUser.getEmail()
        ));

        return updateUser;
    }
}
