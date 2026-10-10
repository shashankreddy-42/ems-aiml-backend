package com.translator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired MockMvc mvc;

    private static final String REGISTER = """
        {"name":"Test","email":"t1@example.com","password":"password123","confirmPassword":"password123"}""";

    @Test
    void registerThenLogin_returnsTokenAndNoPassword() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(REGISTER))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.token").isNotEmpty())
           .andExpect(jsonPath("$.user.password").doesNotExist());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"t1@example.com\",\"password\":\"password123\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void register_withMismatchedPasswords_returns400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"A\",\"email\":\"a@example.com\",\"password\":\"password123\",\"confirmPassword\":\"nope\"}"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void login_withUnknownUser_returns401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"ghost@example.com\",\"password\":\"whatever1\"}"))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mvc.perform(get("/api/history")).andExpect(status().isUnauthorized());
    }

    @Test
    void languages_arePublic() throws Exception {
        mvc.perform(get("/api/languages")).andExpect(status().isOk());
    }
}
