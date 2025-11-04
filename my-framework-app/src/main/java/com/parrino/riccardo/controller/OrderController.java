package com.parrino.riccardo.controller;

import com.parrino.riccardo.annotations.Bean;
import com.parrino.riccardo.annotations.Inject;
import com.parrino.riccardo.service.OrderService;

@Bean
public class OrderController {
    
    @Inject
    private OrderService orderService;
    
}
