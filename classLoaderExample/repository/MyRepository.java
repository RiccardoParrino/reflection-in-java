package classLoaderExample.repository;

import java.util.ArrayList;
import java.util.List;

import classLoaderExample.annotations.Bean;
import classLoaderExample.model.Order;

@Bean
public class MyRepository {
    
    private List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return this.orders;
    }

    public List<Order> addOrder(Order order) {
        orders.add(order);
        return orders;
    }

}
