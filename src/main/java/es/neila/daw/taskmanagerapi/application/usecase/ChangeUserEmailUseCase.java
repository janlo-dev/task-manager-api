package es.neila.daw.taskmanagerapi.application.usecase;

import es.neila.daw.taskmanagerapi.application.dto.ChangeUserEmailRequest;
import es.neila.daw.taskmanagerapi.domain.model.User;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;

public class ChangeUserEmailUseCase {

    private final UserRepository userRepository;

    public ChangeUserEmailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(ChangeUserEmailRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.changeEmail(request.newEmail());

        return userRepository.save(user);
    }
}
