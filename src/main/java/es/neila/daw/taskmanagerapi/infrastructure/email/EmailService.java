package es.neila.daw.taskmanagerapi.infrastructure.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final String FROM = "no-reply@taskmanager.com";

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, ITemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendWelcomeEmail(String toEmail, String userName) {
        Context context = new Context();
        context.setVariable("name", userName);
        String html = templateEngine.process("email/welcome", context);

        // Versión en texto plano para clientes que no muestran HTML
        String text = "Hola " + userName + ",\n\n"
                + "Tu cuenta se ha creado correctamente. Ya puedes iniciar sesión con tu email y contraseña "
                + "para crear tableros, organizar columnas y repartir tareas con tu equipo.\n\n"
                + "¡Gracias por registrarte!";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            // multipart=true para poder enviar texto plano + HTML (multipart/alternative)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(FROM);
            helper.setSubject("¡Bienvenido a Task Manager!");
            helper.setText(text, html);
        } catch (MessagingException e) {
            // Se convierte en MailException para que quien llama la trate como cualquier fallo de envío
            throw new MailPreparationException("Could not build welcome email", e);
        }

        mailSender.send(message);
    }
}
