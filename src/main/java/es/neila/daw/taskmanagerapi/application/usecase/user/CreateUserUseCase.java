package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.CreateUserRequest;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

import java.util.UUID;

public class CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(CreateUserRequest request) {
        User user = new User(
                UUID.randomUUID(),
                request.name(),
                request.email()
        );

        return userRepository.save(user);
    }
}
