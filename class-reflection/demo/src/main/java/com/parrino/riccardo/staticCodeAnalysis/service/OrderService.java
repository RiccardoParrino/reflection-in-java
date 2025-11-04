package com.parrino.riccardo.staticCodeAnalysis.service;

import com.parrino.riccardo.staticCodeAnalysis.annotation.Bean;
import com.parrino.riccardo.staticCodeAnalysis.annotation.Inject;

import com.parrino.riccardo.staticCodeAnalysis.repository.OrderRepository;

@Bean
public class OrderService {
    
    @Inject
    private OrderRepository OrderRepository;

}
