package xyz.cringe.simpletasks.HandlerTest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import xyz.cringe.simpletasks.controller.TaskStatusController;
import xyz.cringe.simpletasks.handler.ValidateExceptionsHandler;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskStatusService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskStatusController.class)
@ContextConfiguration(classes = {TaskStatusController.class, ValidateExceptionsHandler.class})
public class ValidateExceptionsHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskStatusService taskStatusService;

    @MockBean
    private SseEmitterService sseEmitterService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleConstraintViolationException_ShouldReturnBadRequest() throws Exception {
        when(taskStatusService.getAllTaskStatus()).thenThrow(new ConstraintViolationException("Test Constraint Violation", null));

        mockMvc.perform(get("/tasksStatuses/"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Test Constraint Violation"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleArgMismatch_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/tasksStatuses/tasksStatuses_form/qwerty"))
                .andExpect(status().isBadRequest());
    }

}
