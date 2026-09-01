package es.neila.daw.taskmanagerapi.infrastructure.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("no-reply@taskmanager.com");
        message.setSubject("¡Bienvenido a Task Manager!");
        message.setText("Hola " + userName + ",\n\n"
                + "Tu cuenta se ha creado correctamente y ya puedes usar la aplicación con tu email y contraseña.\n\n"
                + "¡Gracias por registrarte!");

        mailSender.send(message);
    }
}
