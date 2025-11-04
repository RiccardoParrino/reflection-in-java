package com.parrino.riccardo.staticCodeAnalysis.repository;

import java.util.List;

import com.parrino.riccardo.model.Order;
import com.parrino.riccardo.staticCodeAnalysis.annotation.Bean;

@Bean
public class OrderRepository {
    
    private List<Order> orders;

    public List<Order> getOrders() {
        return this.orders;
    }

    public List<Order> addOrder(Order order) {
        this.orders.add(order);
        return this.orders;
    }

}
