package classLoaderExample.model;

public class Order {
    private Long id;
    private String name;
    private Double price;

    public void doSomething() {
        System.out.println("this is an object representing an order!");
        System.out.println("id: " + id);
        System.out.println("name: " + name);
        System.out.println("price: " + price);
    }
}
