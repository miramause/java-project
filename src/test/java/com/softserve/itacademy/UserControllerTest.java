package com.softserve.itacademy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import java.util.Arrays;
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
    void postCreateUserWithBindingResultErrorsTest()throws Exception{

        testUser.setLastName("");

        mockMvc.perform(post( "/users/create")
                        .flashAttr("user", testUser))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("create-user"));

    }

    @Test
    void getReadUserTest() throws Exception {
        when(userService.readById(anyLong())).thenReturn(testUser);

        mockMvc.perform(get("/users/{id}/read", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"));
    }

    @Test
    public void getDeleteUserTest() throws Exception {
        mockMvc.perform(get("/users/{id}/delete", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/users/all"));
    }

    @Test
    void getUserUpdate() throws Exception{

        Mockito.doReturn(testUser)
                .when(userService)
                .readById(testUser.getId());

        mockMvc.perform(get("/users/{id}/update",testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("roles", roleService.getAll()))
                .andExpect(status().isOk())
                .andExpect(view().name("update-user"));
    }

    @Test
    void postUpdateUserTest() throws Exception {
        List<Role> roles = Arrays.asList();
        when(userService.readById(anyLong())).thenReturn(testUser);
        when(roleService.getAll()).thenReturn(roles);


        mockMvc.perform(post("/users/{id}/update",testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("user", testUser)
                        .param("roleId", String.valueOf(testUser.getRole().getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + testUser.getId() + "/read"));
    }
}