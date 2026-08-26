package es.neila.daw.taskmanagerapi.domain.port;

public interface DomainEventPublisher {
    void publish(Object event);
}
