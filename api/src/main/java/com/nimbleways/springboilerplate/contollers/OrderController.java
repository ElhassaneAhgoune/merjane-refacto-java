package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private ProductService productService ;

    @PostMapping("{orderId}/processOrder")
    public ResponseEntity<ProcessOrderResponse> processOrder(@PathVariable Long orderId) {
        productService.processOrder(orderId);
        return ResponseEntity.ok(new ProcessOrderResponse(orderId));

    }
}
