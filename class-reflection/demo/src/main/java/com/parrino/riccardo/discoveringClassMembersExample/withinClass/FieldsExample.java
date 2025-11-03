package com.parrino.riccardo.discoveringClassMembersExample.withinClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.parrino.riccardo.model.Order;

public class FieldsExample {
    
    public static void getDeclaredFieldsExample() {
        Order order = new Order();
        for (Field field : order.getClass().getDeclaredFields()) {
            System.out.println(field.getName());
            System.out.println(field.getType());
            System.out.println(Modifier.toString(field.getModifiers()));
        }
    }

    public static void getDeclaredFieldWithExeptionExample() {
        Order order = new Order();
        try {
            order.getClass().getDeclaredField("locationId");
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }

    public static void getDeclaredField() {
        Order order = new Order();
        try {
            Field field = order.getClass().getDeclaredField("orderId");
            System.out.println(field.getName());
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }

}
