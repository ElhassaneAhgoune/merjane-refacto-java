package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.contollers.OrderController;
import com.nimbleways.springboilerplate.dto.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest {

        @Mock
        private ProductService productService;

        @InjectMocks
        private OrderController orderController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        }

        @Test
        void testProcessOrder_success() throws Exception {
                Long orderId = 1L;

                mockMvc.perform(post("/orders/{orderId}/processOrder", orderId))
                        .andExpect(status().isOk());

                verify(productService, times(1)).processOrder(orderId);
        }


        @Test
        void testProcessOrder_returnResponseEntity() {
                Long orderId = 1L;
                ResponseEntity<ProcessOrderResponse> response = orderController.processOrder(orderId);
                assert(response.getStatusCode() == HttpStatus.OK);
                assert(response.getBody() != null);
                assert(response.getBody().id().equals(orderId));
        }
}
