package classLoaderExample.controller;

import classLoaderExample.annotations.Bean;
import classLoaderExample.annotations.GetMapping;
import classLoaderExample.annotations.Inject;
import classLoaderExample.annotations.PostMapping;
import classLoaderExample.model.Order;
import classLoaderExample.service.MyService;

@Bean
public class MyController {
    
    @Inject
    private MyService myService;

    @PostMapping(name = "createOrder")
    public void createOrder(Order order) {
        this.myService.addOrder(order);
    }

    @GetMapping(name = "printOrders")
    public void printOrder() {
        this.myService.getOrders().forEach(s -> System.out.println(s.getName()));
    }

    public void printMyService() {
        System.out.println(myService);
    }

}
