package xyz.cringe.simpletasks.InterceptorTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.cringe.simpletasks.controller.TaskStatusController;
import xyz.cringe.simpletasks.interceptor.HxRequestInterceptor;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskStatusService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskStatusController.class)
@Import(HxRequestInterceptor.class)
class HxRequestInterceptorTest implements WebMvcConfigurer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private TaskStatusService taskStatusService;

    @MockBean
    private SseEmitterService sseEmitterService;

    @Autowired
    private HxRequestInterceptor hxRequestInterceptor;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hxRequestInterceptor);
    }

    @Test
    void whenHxRequestHeaderIsPresent_thenProceed() throws Exception {
        mockMvc.perform(get("/tasksStatuses/taskStatuses_form")
                        .header("HX-Request", "true")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void whenHxRequestHeaderIsAbsent_thenRedirect() throws Exception {
        mockMvc.perform(get("/tasksStatuses/taskStatuses_form"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
