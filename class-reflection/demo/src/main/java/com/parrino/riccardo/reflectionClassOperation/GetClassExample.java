package com.parrino.riccardo.reflectionClassOperation;

import com.parrino.riccardo.model.Order;

public class GetClassExample {
    
    public static void getNameExample() {
        Order order = new Order();
        System.out.println(order.getClass().getName());
    }

    public static void getSimpleNameExample() {
        Order order = new Order();
        System.out.println(order.getClass().getSimpleName());
    }

}
