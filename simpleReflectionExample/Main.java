package simpleReflectionExample;

public class Main {
    
    public static void main (String [] args) throws Exception {
        // find all classes
        // find annotation
        Class<?> clazz = Class.forName("simpleReflectionExample.MyController");

        Object obj = clazz.getDeclaredConstructor().newInstance();

        System.out.println("Loaded class: " + clazz.getName());
    }

}
