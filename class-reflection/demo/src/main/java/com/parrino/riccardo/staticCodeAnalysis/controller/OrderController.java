package com.parrino.riccardo.staticCodeAnalysis.controller;

import com.parrino.riccardo.staticCodeAnalysis.annotation.Bean;
import com.parrino.riccardo.staticCodeAnalysis.annotation.Inject;
import com.parrino.riccardo.staticCodeAnalysis.service.OrderService;

@Bean
public class OrderController {
    
    @Inject
    private OrderService orderService;

}
