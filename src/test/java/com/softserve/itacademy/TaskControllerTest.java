package com.softserve.itacademy;


import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ToDoService toDoService;

    @MockBean
    private StateService stateService;

    @Test
    void getCreate() throws Exception{
        Mockito.doReturn(new ToDo())
                .when(toDoService)
                .readById(5);

        ToDo expected = toDoService.readById(5);

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/tasks/create/todos/5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attribute("todo", expected))
                .andExpect(MockMvcResultMatchers.model().attribute("priorities", Priority.values()));
    }

    @Test
    void postCreate() throws Exception{
        TaskDto taskDto = new TaskDto();
        taskDto.setName("Name");
        taskDto.setPriority("LOW");
        taskDto.setTodoId(5);

        Mockito.doReturn(new ToDo())
                .when(toDoService)
                .readById(taskDto.getTodoId());
        Mockito.doReturn(new State())
                .when(stateService)
                .getByName("New");

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/tasks/create/todos/5")
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("todoId", String.valueOf(taskDto.getTodoId())))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/todos/5/tasks"));
    }

    @Test
    void postCreateWithBindingResultErrors()throws Exception{
        TaskDto taskDto = new TaskDto();
        taskDto.setName("");
        taskDto.setPriority("LOW");
        taskDto.setTodoId(5);

        ToDo toDo = new ToDo();

        Mockito.doReturn(toDo)
                .when(toDoService)
                .readById(taskDto.getTodoId());

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/tasks/create/todos/5")
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("todoId", String.valueOf(taskDto.getTodoId())))
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("todo", toDo))
                .andExpect(MockMvcResultMatchers.model().attribute("priorities", Priority.values()))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void getUpdate() throws Exception{
        List<State> states = Arrays.asList(new State(), new State(), new State());

        Task task = new Task();
        task.setId(1);
        task.setName("name");
        task.setPriority(Priority.LOW);
        task.setTodo(new ToDo());
        task.setState(new State());

        Mockito.doReturn(task)
                .when(taskService)
                .readById(1);
        Mockito.doReturn(states)
                .when(stateService)
                .getAll();

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/tasks/1/update/todos/5"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("task"))
                .andExpect(MockMvcResultMatchers.model().attribute("priorities", Priority.values()))
                .andExpect(MockMvcResultMatchers.model().attribute("states", states))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void postUpdate() throws Exception{
        Task task = new Task();
        task.setId(1);
        task.setName("Name");
        task.setPriority(Priority.LOW);
        task.setTodo(new ToDo());
        task.setState(new State());
        task.getTodo().setId(5);

        TaskDto taskDto = TaskTransformer.convertToDto(task);

        Mockito.doReturn(task.getTodo())
                .when(toDoService)
                .readById(5);
        Mockito.doReturn(new State())
                .when(stateService)
                .readById(0);
        Mockito.doReturn(task)
                .when(taskService)
                .update(task);

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/tasks/1/update/todos/5")
                        .param("id", String.valueOf(taskDto.getId()))
                        .param("name", taskDto.getName())
                        .param("priority", taskDto.getPriority())
                        .param("todoId", String.valueOf(taskDto.getTodoId()))
                        .param("stateId", String.valueOf(taskDto.getStateId())))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/todos/5/tasks"));
    }

    @Test
    void postUpdateWithBindingResultErrors() throws Exception{

        List<State> states = Arrays.asList(new State(), new State(), new State());

        Task task = new Task();
        task.setId(1);
        task.setName("");
        task.setPriority(Priority.LOW);
        task.setTodo(new ToDo());
        task.setState(new State());
        task.getTodo().setId(5);

        TaskDto taskDto = TaskTransformer.convertToDto(task);

        Mockito.doReturn(states)
                .when(stateService)
                .getAll();

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/tasks/1/update/todos/5")
                        .param("id", String.valueOf(taskDto.getId()))
                        .param("name", taskDto.getName())
                        .param("todoId", String.valueOf(taskDto.getTodoId()))
                        .param("stateId", String.valueOf(taskDto.getTodoId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().hasErrors())
                .andExpect(MockMvcResultMatchers.model().attribute("priorities", Priority.values()))
                .andExpect(MockMvcResultMatchers.model().attribute("states", states));
    }

    @Test
    void delete() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/tasks/1/delete/todos/5"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/todos/5/tasks"));
    }

}