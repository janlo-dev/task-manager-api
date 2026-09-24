package es.neila.daw.taskmanagerapi.infrastructure;

import com.jayway.jsonpath.JsonPath;
import es.neila.daw.taskmanagerapi.domain.repository.UserRepository;
import es.neila.daw.taskmanagerapi.infrastructure.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EmailService emailService;

    // Email distinto en cada test: comparten la misma BD H2 del contexto
    private final String email = "user-" + UUID.randomUUID() + "@test.com";

    private ResultActions register(String email) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Ana", "email": "%s", "password": "1234abcd"}
                        """.formatted(email)));
    }

    private String registerAndGetToken(String email) throws Exception {
        String body = register(email).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    @Test
    void register_withNewEmail_createsUserAndSendsWelcomeEmail() throws Exception {
        register(email)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        assertThat(userRepository.findByEmail(email)).isPresent();
        verify(emailService).sendWelcomeEmail(email, "Ana");
    }

    @Test
    void register_withExistingEmail_returns400WithoutCreatingAnotherUser() throws Exception {
        register(email).andExpect(status().isCreated());
        UUID firstUserId = userRepository.findByEmail(email).orElseThrow().getId();

        register(email)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already in use"));

        // Si se hubiera creado un duplicado, findByEmail fallaría por encontrar dos filas
        assertThat(userRepository.findByEmail(email).orElseThrow().getId()).isEqualTo(firstUserId);
    }

    @Test
    void register_whenWelcomeEmailFails_stillCreatesUserAndReturns201() throws Exception {
        doThrow(new MailSendException("SMTP server down"))
                .when(emailService).sendWelcomeEmail(any(), any());

        register(email)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        assertThat(userRepository.findByEmail(email)).isPresent();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "1234abcd"}
                                """.formatted(email)))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/boards/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/boards/me").header("Authorization", "Bearer not-a-valid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withValidTokenButNoAccess_returns403() throws Exception {
        String token = registerAndGetToken(email);

        mockMvc.perform(get("/api/columns/board/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_withValidToken_returns200() throws Exception {
        String token = registerAndGetToken(email);

        mockMvc.perform(get("/api/boards/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
