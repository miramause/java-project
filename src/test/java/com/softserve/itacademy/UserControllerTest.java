package com.softserve.itacademy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.softserve.itacademy.controller.UserController;
import com.softserve.itacademy.model.Role;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ToDoListApplication.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

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
    public void getAllUsers() throws Exception {
        List<User> expected = userService.getAll();

        mockMvc.perform(get("/users/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", expected));

    }

    @Test
    void getCreateUserTest() throws Exception {
        mockMvc.perform(get("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));
    }

    @Test
    public void postCreateUserTest() throws Exception {
        testUser.setEmail("user34@gmail.com");
        when(userService.create(any(User.class))).thenReturn(testUser);
        mockMvc.perform(post("/users/create").contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("user", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + testUser.getId()));
    }

    @Test
    void postCreateUserWithBindingResultErrors()throws Exception{

        testUser.setLastName("");

        mockMvc.perform(post( "/users/create")
                        .flashAttr("user", testUser))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk());

    }
    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(get("/users/{id}/delete", 4)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/users/all"));
    }

    @Test
    void getUserUpdate() throws Exception{

        Mockito.doReturn(testUser)
                .when(userService)
                .readById(4);

        mockMvc.perform(get("/users/{id}/update",4)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("roles", roleService.getAll()))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"));
    }
}