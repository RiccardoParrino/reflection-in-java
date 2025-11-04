package com.parrino.riccardo.service;

import com.parrino.riccardo.annotations.Bean;
import com.parrino.riccardo.annotations.Inject;
import com.parrino.riccardo.repository.OrderRepository;

@Bean
public class OrderService {
    
    @Inject
    private OrderRepository orderRepository;

}
