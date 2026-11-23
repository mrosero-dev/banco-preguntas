package co.edu.unicauca.identity.api.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unicauca.identity.application.IdentityService;
import co.edu.unicauca.identity.domain.exception.UserNotFoundException;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class IdentityControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentityService service;

    @Test
    void shouldListUsersByRole() throws Exception {
        when(service.findUsers(Role.REVIEWER)).thenReturn(List.of(
                User.restore(4L, "Rev", "r@b.com", "hash", Role.REVIEWER)));

        mockMvc.perform(get("/api/users").param("role", "REVIEWER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("r@b.com"));
    }

    @Test
    void shouldFindUserById() throws Exception {
        when(service.findById(2L)).thenReturn(
                User.restore(2L, "Ana", "a@b.com", "hash", Role.AUTHOR));

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ana"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(service.findById(9L))
                .thenThrow(new UserNotFoundException("User not found: 9"));

        mockMvc.perform(get("/api/users/9"))
                .andExpect(status().isNotFound());
    }
}
