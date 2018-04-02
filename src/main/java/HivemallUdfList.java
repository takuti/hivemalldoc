import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Set;

public class HivemallUdfList {
    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Description.class);
        for (Class<?> clazz : annotated) {
            Description desc = clazz.getAnnotation(Description.class);
            System.out.println(desc.name() + " " + desc.value());
        }
    }
}
