package classLoaderExample.service;

import java.util.List;

import classLoaderExample.annotations.Bean;
import classLoaderExample.annotations.Inject;
import classLoaderExample.model.Order;
import classLoaderExample.repository.MyRepository;

@Bean
public class MyService {
    
    @Inject
    private MyRepository myRepository;

    public void addOrder(Order order) {
        this.myRepository.addOrder(order);
    }

    public List<Order> getOrders() {
        return this.myRepository.getOrders();
    }

    public void printMyRepository() {
        System.out.println(myRepository);
    }

}
