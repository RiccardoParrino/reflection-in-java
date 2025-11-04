package com.parrino.riccardo.repository;

import java.util.List;

import com.parrino.riccardo.annotations.Bean;
import com.parrino.riccardo.model.Order;

@Bean
public class OrderRepository {
    
    private List<Order> orders;

    public List<Order> addOrder (Order order) {
        this.orders.add(order);
        return this.orders;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

}
