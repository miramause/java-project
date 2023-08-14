package com.softserve.itacademy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.softserve.itacademy.controller.UserController;
import com.softserve.itacademy.model.Role;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.RoleRepository;

import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ToDoListApplication.class)
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;


    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setName("USER");

        user = new User();
        user.setId(11L);
        user.setEmail("newUser@mail.com");
        user.setRole(role);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("qwQW12!@");
    }

    @Test
    public void getAllUsers() throws Exception {
        List<User> expected = userService.getAll();

        mockMvc.perform(get("/users/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", expected));

    }

    @Test
    void createUserViewTest() throws Exception {
        mockMvc.perform(get("/users/create" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));
    }
    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/"+ user.getId()));
    }

    @Test
    public void testReadUser() throws Exception {
        mockMvc.perform(get("/users/{id}/read", 5)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testReadNonExistUser() throws Exception {
        mockMvc.perform(get("/users/{id}/read", 9)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}/delete", 6)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testDeleteNonExistUser() throws Exception {
        mockMvc.perform(delete("/users/{id}/delete", 9)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }


    @Test
    public void testUpdateUser() throws Exception {
        User updateUser= new User();
        updateUser.setLastName("UpdateFN");
        updateUser.setLastName("UpdateLN");
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/update", 6)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testUpdateNonExistUser() throws Exception {
        User updateUser= new User();
        updateUser.setLastName("UpdateFN");
        updateUser.setLastName("UpdateLN");
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}/update", 7)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

    }
}