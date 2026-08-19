package es.neila.daw.taskmanagerapi.application.usecase.user;

import es.neila.daw.taskmanagerapi.application.dto.RenameUserRequest;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

public class RenameUserUseCase {

    private final UserRepository userRepository;

    public RenameUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(RenameUserRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.rename(request.newName());

        return userRepository.save(user);
    }
}
