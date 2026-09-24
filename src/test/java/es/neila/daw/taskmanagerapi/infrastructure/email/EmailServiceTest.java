package es.neila.daw.taskmanagerapi.infrastructure.email;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private final Session session = Session.getInstance(new Properties());

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Mismo motor (SpringEL) y mismo prefijo/sufijo que la autoconfiguración de Spring Boot
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        emailService = new EmailService(mailSender, templateEngine);
    }

    // Envía el correo y lo devuelve tal como viajaría por SMTP (serializado y vuelto a leer)
    private MimeMessage sendAndCapture(String to, String name) throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));

        emailService.sendWelcomeEmail(to, name);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        captor.getValue().writeTo(out);
        return new MimeMessage(session, new ByteArrayInputStream(out.toByteArray()));
    }

    // Busca recursivamente la primera parte con ese tipo MIME dentro del multipart
    private String findPart(Part part, String mimeType) throws MessagingException, IOException {
        if (part.isMimeType(mimeType)) {
            return (String) part.getContent();
        }
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart child = multipart.getBodyPart(i);
                String found = findPart(child, mimeType);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test
    void sendWelcomeEmail_setsRecipientSenderAndSubject() throws Exception {
        MimeMessage message = sendAndCapture("ana@test.com", "Ana");

        assertThat(message.getAllRecipients()).containsExactly(new InternetAddress("ana@test.com"));
        assertThat(message.getFrom()).containsExactly(new InternetAddress("no-reply@taskmanager.com"));
        assertThat(message.getSubject()).isEqualTo("¡Bienvenido a Task Manager!");
    }

    @Test
    void sendWelcomeEmail_includesHtmlAndPlainTextVersions() throws Exception {
        MimeMessage message = sendAndCapture("ana@test.com", "Ana");

        String html = findPart(message, "text/html");
        String text = findPart(message, "text/plain");

        assertThat(html).contains("Task Manager").contains("¡Hola, <span>Ana</span>!");
        assertThat(text).startsWith("Hola Ana,").contains("¡Gracias por registrarte!");
    }

    @Test
    void sendWelcomeEmail_escapesHtmlInUserName() throws Exception {
        MimeMessage message = sendAndCapture("ana@test.com", "<script>alert('x')</script>");

        String html = findPart(message, "text/html");

        assertThat(html).doesNotContain("<script>").contains("&lt;script&gt;");
    }

    @Test
    void sendWelcomeEmail_whenMessageCannotBeBuilt_throwsMailExceptionWithoutSending() throws Exception {
        MimeMessage broken = mock(MimeMessage.class);
        doThrow(new MessagingException("broken")).when(broken).setContent(any(Multipart.class));
        when(mailSender.createMimeMessage()).thenReturn(broken);

        assertThatThrownBy(() -> emailService.sendWelcomeEmail("ana@test.com", "Ana"))
                .isInstanceOf(MailException.class)
                .hasCauseInstanceOf(MessagingException.class);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
