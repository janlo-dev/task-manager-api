package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.RenameUserRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.UUID;

public class RenameUserUseCase {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public RenameUserUseCase(UserRepository userRepository, DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public User execute(RenameUserRequest request, UUID performedByUserId) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.rename(request.newName());

        User updateUser =  userRepository.save(user);

        eventPublisher.publish(new AuditDomainEvent(
                updateUser.getId(),
                "USER",
                "RENAMED",
                performedByUserId,
                "User rename to: " + updateUser.getName()
        ));

        return updateUser;
    }
}
