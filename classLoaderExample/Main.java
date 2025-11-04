package classLoaderExample;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import classLoaderExample.annotations.Inject;

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
        List<File> files = ClassDiscovererService.run("classLoaderExample"); // find files
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

        // topsort incorrect
        System.out.println();
        topsort.forEach(s -> System.out.print(s + " -> "));

        System.out.println();
        reorderBeans.forEach(s -> System.out.print(s.getSimpleName() + " -> "));
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
            System.out.println("Inspecting... " + beans.get(i).getSimpleName());
            Field[] fields = beans.get(i).getDeclaredFields();

            for ( Field field : fields ) {
                System.out.println(field.getName());
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