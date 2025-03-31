package com.example.sd_backend2.controller;

import com.example.sd_backend2.dto.SubscriptionDTO;
import com.example.sd_backend2.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubscriptionController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {com.example.sd_backend2.security.JwtAuthenticationFilter.class})
)
@ActiveProfiles("test")
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testSubscribe() throws Exception {
        Mockito.when(subscriptionService.subscribe(eq(1L), eq("testUser")))
                .thenReturn("Subscription successful");

        mockMvc.perform(post("/api/subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription successful"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testUnsubscribe() throws Exception {
        Mockito.when(subscriptionService.unsubscribe(eq(1L), eq("testUser")))
                .thenReturn("Unsubscribed successfully");

        mockMvc.perform(delete("/api/subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Unsubscribed successfully"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetSubscriptions() throws Exception {
        SubscriptionDTO dto = new SubscriptionDTO();
        // Assume SubscriptionDTO has a setter for subscribedToId
        dto.setSubscribedToId(1L);
        List<SubscriptionDTO> list = Collections.singletonList(dto);

        Mockito.when(subscriptionService.getSubscriptions("testUser")).thenReturn(list);

        mockMvc.perform(get("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subscribedToId").value(1));
    }
}
