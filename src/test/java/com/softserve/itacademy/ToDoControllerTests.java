package com.softserve.itacademy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.softserve.itacademy.controller.ToDoController;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ToDoController.class)
@ContextConfiguration(classes = ToDoListApplication.class)
public class ToDoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoService toDoService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private ToDo testToDo;

    private User testUser;


    @BeforeEach
    public void setUp() {
        testToDo = new ToDo();
        testUser = new User();
        testUser.setId(44L);
        testUser.setEmail("test@gmail.com");
        testToDo.setOwner(testUser);
        testToDo.setTitle("build app");
        when(userService.readById(1L)).thenReturn(testUser);
        when(toDoService.create(any(ToDo.class))).thenReturn(testToDo);
    }

    @Test
    void createToDoViewTest() throws Exception {
        mockMvc.perform(get("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo)
                        .flashAttr("ownerId", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"));
    }

    @Test
    void createToDoTest() throws Exception {
        mockMvc.perform(post("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo)
                        .flashAttr("ownerId", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + + testUser.getId()));
    }


    @Test
    void createToDoWithEmptyTitleTest() throws Exception {
        testToDo.setTitle(null);
        mockMvc.perform(post("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo)
                        .flashAttr("ownerId", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"));
    }

}