package com.parrino.riccardo.discoveringClassMembersExample.withinClass;

import java.lang.reflect.Constructor;

import com.parrino.riccardo.model.Order;

public class ConstructorsExample {
    
    public static void getConstructorsExample() {
        Order order = new Order();
        for ( Constructor<?> c : order.getClass().getConstructors() ) {
            System.out.println(c.getParameterCount());
            System.out.println(c.getDeclaringClass().getSimpleName());
        }
    }

    public static void getDeclaredConstructorsExample() {
        Order order = new Order();
        for ( Constructor<?> c : order.getClass().getDeclaredConstructors() ) {
            System.out.println(c.getParameterCount());
            System.out.println(c.getDeclaringClass().getSimpleName());
        }
    }

    public static void getDeclaredConstructorExample() {
        Order order = new Order();
    }

}
