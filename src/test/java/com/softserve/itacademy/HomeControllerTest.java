package com.softserve.itacademy;

import com.softserve.itacademy.controller.HomeController;
import com.softserve.itacademy.model.Role;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HomeController homeController;

    private User testUser;

    private Role testRole;

    @BeforeEach
    public void setUp() {
        testRole = new Role();
        testRole.setName("USER");

        testUser = new User();
        testUser.setId(11L);
        testUser.setEmail("newUser@mail.com");
        testUser.setRole(testRole);
        testUser.setFirstName("Firstname");
        testUser.setLastName("Lastname");
        testUser.setPassword("qwQW12!@");
    }
    @Test
    public void contextLoads() {
        assertThat(homeController).isNotNull();
    }

    @Test
    void getHomeTest() throws Exception {
        when(userService.create(any(User.class))).thenReturn(testUser);
        mockMvc.perform(get("/")
                        .flashAttr("users", userService.getAll()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void getHomeWithHomeUrlTest() throws Exception {
        when(userService.create(any(User.class))).thenReturn(testUser);

        mockMvc.perform(get("/home")
                        .flashAttr("users", userService.getAll()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}
