package com.softserve.itacademy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.softserve.itacademy.controller.ToDoController;
import com.softserve.itacademy.model.*;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import java.util.ArrayList;
import java.util.List;
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
        testUser = new User();
        testUser.setId(44L);
        testUser.setFirstName("Jack");
        testUser.setLastName("Richardson");
        testUser.setPassword("jack_password");
        testUser.setEmail("test@gmail.com");

        testToDo = new ToDo();
        testToDo.setOwner(testUser);
        testToDo.setTitle("build app");
    }

    @Test
    void createToDoViewTest() throws Exception {
        when(userService.readById(1L)).thenReturn(testUser);
        when(toDoService.create(any(ToDo.class))).thenReturn(testToDo);

        mockMvc.perform(get("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo)
                        .flashAttr("ownerId", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"));
    }

    @Test
    void createToDoTest() throws Exception {
        when(userService.readById(1L)).thenReturn(testUser);
        when(toDoService.create(any(ToDo.class))).thenReturn(testToDo);

        mockMvc.perform(post("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo)
                        .flashAttr("ownerId", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + +testUser.getId()));
    }


    @Test
    void createToDoWithEmptyTitleTest() throws Exception {
        testToDo.setTitle(null);
        mockMvc.perform(post("/todos/create/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("create-todo"));
    }

    @Test
    void readToDoTest() throws Exception {
        when(toDoService.readById(anyLong())).thenReturn(testToDo);
        when(taskService.getByTodoId(anyLong())).thenReturn(generateTasks());
        when(userService.getAll()).thenReturn(generateUsers());

        mockMvc.perform(get("/todos/{id}/tasks", testToDo.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"));
    }

    @Test
    void updateToDoViewTest() throws Exception {
        when(toDoService.readById(anyLong())).thenReturn(testToDo);

        mockMvc.perform(get("/todos/" + testToDo.getId() + "/update/users/" + testToDo.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("update-todo"));
    }

    @Test
    void updateToDoTest() throws Exception {
        when(toDoService.readById(anyLong())).thenReturn(testToDo);

        mockMvc.perform(post("/todos/" + testToDo.getId() + "/update/users/" + testToDo.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/" + testToDo.getOwner().getId()));
    }

    @Test
    void updateToDoWithBindingResultErrorsTest() throws Exception {
        testToDo.setTitle("");

        when(userService.readById(anyLong())).thenReturn(testUser);

        mockMvc.perform(post("/todos/" + testToDo.getId() + "/update/users/" + testToDo.getOwner().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("todo", testToDo))
                .andExpect(model().hasErrors())
                .andExpect(status().isOk())
                .andExpect(view().name("update-todo"));
    }

    @Test
    void deleteToDo() throws Exception {
        mockMvc.perform(get("/todos/" + testToDo.getId() + "/delete/users/ " + testToDo.getOwner().getId()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/todos/all/users/" + testToDo.getOwner().getId()));
    }

    @Test
    void getAllTodosTest() throws Exception {
        when(toDoService.getByUserId(anyLong())).thenReturn(generateTodos());
        when(userService.readById(anyLong())).thenReturn(testUser);

        List<ToDo> myTodos = generateTodos();
        testUser.setMyTodos(myTodos);

        mockMvc.perform(get("/todos/all/users/ " + testToDo.getOwner().getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"));

        assertEquals(1, testUser.getMyTodos().size());
    }

    @Test
    void addCollaboratorTest() throws Exception {
        User existingCollaborator = generateExistingCollaborator();
        User newCollaborator = generateNewCollaborator();

        List<User> users = new ArrayList<>();
        users.add(existingCollaborator);

        testToDo.setId(999);
        testToDo.setCollaborators(users);

        when(toDoService.readById(anyLong())).thenReturn(testToDo);
        when(userService.readById(existingCollaborator.getId())).thenReturn(existingCollaborator);
        when(userService.readById(newCollaborator.getId())).thenReturn(newCollaborator);

        mockMvc.perform(get("/todos/" + testToDo.getId() + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("user_id", String.valueOf(newCollaborator.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/" + testToDo.getId() + "/tasks"));

        assertEquals(2, testToDo.getCollaborators().size());
    }


    @Test
    void removeCollaboratorTest() throws Exception {
        User existingCollaborator = generateExistingCollaborator();
        User newCollaborator = generateNewCollaborator();

        List<User> users = new ArrayList<>();
        users.add(existingCollaborator);

        testToDo.setId(999);
        testToDo.setCollaborators(users);

        when(toDoService.readById(anyLong())).thenReturn(testToDo);
        when(userService.readById(existingCollaborator.getId())).thenReturn(existingCollaborator);
        when(userService.readById(newCollaborator.getId())).thenReturn(newCollaborator);

        mockMvc.perform(get("/todos/" + testToDo.getId() + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("user_id", String.valueOf(newCollaborator.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/" + testToDo.getId() + "/tasks"));

        assertEquals(1, testToDo.getCollaborators().size());

    }

    private List<Task> generateTasks() {
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setPriority(Priority.HIGH);
        State state = new State();
        state.setName("Open");
        task.setState(state);
        task.setName("clean cache");
        testToDo.setId(4L);
        task.setTodo(testToDo);
        tasks.add(task);
        return tasks;
    }

    private List<User> generateUsers() {
        List<User> users = new ArrayList<>();
        return users;
    }

    private User generateNewCollaborator() {
        User newCollaborator = new User();
        newCollaborator.setId(24L);
        newCollaborator.setFirstName("John");
        newCollaborator.setLastName("Doe");
        newCollaborator.setEmail("j.doe@gmail.com");
        newCollaborator.setPassword("password");
        return newCollaborator;
    }

    private User generateExistingCollaborator() {
        User existingCollaborator = new User();
        existingCollaborator.setId(23L);
        existingCollaborator.setFirstName("Jane");
        existingCollaborator.setLastName("Rand");
        existingCollaborator.setEmail("j.rand@gmail.com");
        existingCollaborator.setPassword("password");
        return existingCollaborator;
    }


    private List<ToDo> generateTodos() {
        List<ToDo> toDos = new ArrayList<>();
        ToDo toDo = new ToDo();
        toDo.setId(441);
        toDo.setTitle("Deliver project");
        toDo.setOwner(testUser);
        toDos.add(toDo);
        return toDos;
    }

}