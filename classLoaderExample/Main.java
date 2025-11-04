package classLoaderExample;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import classLoaderExample.annotations.Controller;
import classLoaderExample.annotations.GetMapping;
import classLoaderExample.annotations.Inject;
import classLoaderExample.annotations.PostMapping;
import classLoaderExample.controller.MyController;
import classLoaderExample.model.Order;

public class Main {

    // find classes
    // filter out Main class
    // remove non bean file
    // build dag
        // for every class
        // find field (to find dependency)
    // sort in topological order
    // instantiate classes
    
    public static void main (String [] args) throws Exception {
        List<Object> applicationContext = MyFramework.run("classLoaderExample");
        MyDispatcherServlet myDispatcherServlet = new MyDispatcherServlet();
        myDispatcherServlet.register(applicationContext);
        myDispatcherServlet.printAllEndpoint();

        MyController myController = (MyController) applicationContext.get(2);
        Order order1 = new Order().setName("firstOrder");
        Order order2 = new Order().setName("secondOrder");
        Order order3 = new Order().setName("thirdOrder");
        myController.createOrder(order1);
        myController.createOrder(order2);
        myController.createOrder(order3);

        myDispatcherServlet.listen();
    }

}

class MyDispatcherServlet {

    private Map<String, ObjectMethodReference> register = new HashMap<>();

    class ObjectMethodReference {

        private Object object;
        private Method method;

        public Object getObject() {
            return this.object;
        }

        public ObjectMethodReference setObject(Object object) {
            this.object = object;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public ObjectMethodReference setMethod(Method method) {
            this.method = method;
            return this;
        }

    }

    // blocking style listener
    public void listen () throws IllegalAccessException, InvocationTargetException {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            try {
                String inputLine = scanner.nextLine();
                String methodName = inputLine.split(" ")[0];
                
                ObjectMethodReference objectMethodReference = register.get(methodName);
                objectMethodReference.getMethod().invoke(objectMethodReference.getObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(List<Object> applicationContext) {
        for ( Object o : applicationContext ) {
            if (o.getClass().getAnnotation(Controller.class) != null) {
                for(Method method : o.getClass().getMethods()) {
                    if ( method.getAnnotation(GetMapping.class) != null ) {
                        String methodEndpoint = method.getAnnotation(GetMapping.class).name();
                        register.put(methodEndpoint, new ObjectMethodReference().setMethod(method).setObject(o));
                    }
                    if ( method.getAnnotation(PostMapping.class) != null ) {
                        String methodEndpoint = method.getAnnotation(PostMapping.class).name();
                        register.put(methodEndpoint, new ObjectMethodReference().setMethod(method).setObject(o));
                    }
                }
            }
        }
    }

    public void printAllEndpoint() {
        register.keySet().forEach(System.out::println);
    }

}

class MyFramework {

    public static List<Object> run (String pathname) throws Exception {
        List<File> files = ClassDiscovererService.run(pathname); // find files
        // files.remove("Main.java"); // filter out Main.java file
        List<Class<?>> clazz = ClassLoaderService.run(files); // find object
        List<Class<?>> beans = FilterBeans.run(clazz); // find beans
        List<List<Integer>> dag = DAGBuilder.run(beans); // build dag
        List<Integer> topsort = TopologicalSort.run(dag); // make topological sort
        List<Class<?>> reorderBeans = topsort.stream().map(i -> beans.get(i)).collect(Collectors.toList()); // reorder beans for instatiation
        List<Object> applicationContext = reorderBeans.stream().map(c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()); // instantiate bean
        DependencyInjectionResolver.run(applicationContext); // satisfy dependency injection
        return applicationContext;
    }

}

class DependencyInjectionResolver {

    public static void run (List<Object> applicationContext) throws IllegalArgumentException, IllegalAccessException {
        for ( Object bean : applicationContext ) {
            for (Field f : bean.getClass().getDeclaredFields()) {
                if (f.getAnnotation(Inject.class) != null) {
                    f.setAccessible(true);
                    for ( Object o : applicationContext ) {
                        if (o.getClass().getSimpleName().equals(f.getType().getSimpleName())) {
                            f.set(bean,o);
                        }
                    }
                }   
            }
        }
    }

}

class TopologicalSort {

    // making topological sort using dfs
    public static List<Integer> run (List<List<Integer>> dag) {
        List<Integer> sorted = new ArrayList<>();
        Boolean[] visited = new Boolean[dag.size()];
        for ( int i = 0; i < visited.length; i++ )
            visited[i] = false;
        dfs(dag, 0, visited, sorted);
        return sorted;
    }

    private static void dfs(List<List<Integer>> dag, Integer currentNode, Boolean[] visited, List<Integer> sorted) {
        while( ! Arrays.stream(visited).reduce(true, (a,b) -> a & b) ) {
            for (int i = 0; i < visited.length; i++) 
                if (visited[i] == false) 
                    subroutine(dag, i, visited, sorted);
        }
    }

    private static void subroutine(List<List<Integer>> dag, Integer currentNode, Boolean[] visited, List<Integer> sorted) {
        if ( dag.get(currentNode).size() == 0 ) {
            visited[currentNode] = true;
            sorted.add(currentNode);
            return;
        } else {
            visited[currentNode] = true;
            for(Integer node : dag.get(currentNode)) {
                if (visited[node] == false)
                    subroutine(dag, node, visited, sorted);
            }
            sorted.add(currentNode);
            return;
        }
    }

}

class DAGBuilder {

    public static List<List<Integer>> run (List<Class<?>> beans) {
        List<List<Integer>> dag = new ArrayList<>();
        for ( int i = 0; i < beans.size(); i++ ) {
            dag.add(new ArrayList<>());
        }

        for ( int i = 0; i < beans.size(); i++ ) {
            Field[] fields = beans.get(i).getDeclaredFields();

            for ( Field field : fields ) {
                if (field.getAnnotation(Inject.class) != null) {
                    for (int j = 0; j < beans.size(); j++) {
                        if (beans.get(j).getSimpleName().equals(field.getType().getSimpleName())) {
                            dag.get(i).add(j);
                        }
                    }
                }

            }
        }
        return dag;
    }
}

class FilterBeans {

    public static List<Class<?>> run( List<Class<?>> clazzs ) {
        List<Class<?>> beans = new ArrayList<>();
        for( Class<?> clazz : clazzs ) {
            Annotation[] annotations = clazz.getAnnotations();
            if ( Arrays.stream(annotations)
                    .map(ann -> ann.annotationType().getSimpleName().equals("Bean"))
                    .reduce(false, (a,b) -> a || b) )
                beans.add(clazz);
            
        }
        return beans;
    }

}

class ClassLoaderService {

    public static List<Class<?>> run(List<File> files) {
        List<Class<?>> clazzs = new ArrayList<>();
        try {
            for ( File f : files ) {
                if ( ! f.getPath().split("\\.")[0].endsWith("Main") ) {
                    System.out.println("Reading..." + f.getPath().split("\\.")[0]);
                    URL classUrl = new URL("file:\\"+f.getPath().split("\\.")[0]);
                    URLClassLoader loader = new URLClassLoader(new URL[]{classUrl});
                    Class<?> clazz = loader.loadClass(f.getPath().split("\\.")[0].replace("\\", "."));
                    clazzs.add(clazz);
                    System.out.println("Loaded class..." + f.getPath().split("\\.")[0]);
                    System.out.println("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clazzs;
    }

}

class ClassDiscovererService {

    public static List<File> run (String pathname) {
        File[] files = new File(pathname).listFiles();
        List<File> bfs = new ArrayList<>();
        List<File> fileList = new ArrayList<>();
        
        for ( File f : files ) {
            if (f.isDirectory())
                bfs.add(f);
            else
                fileList.add(f);
        }

        for (File f : bfs) {
            fileList.addAll(run(f.getPath()));
        }

        return fileList;
    }
    
}