package com.parrino.riccardo.discoveringClassMembersExample.withinClass;

import java.lang.reflect.Method;

import com.parrino.riccardo.model.Order;

public class MethodsExample {
    
    public static void getDeclaredMethods() {
        Order order = new Order();
        Method[] methods = order.getClass().getDeclaredMethods();
        for (Method m : methods) {
            System.out.println(m.getName());
            System.out.println(m.getReturnType());
            System.out.println(m.getParameterCount());
        }
    }

}
