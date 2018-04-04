import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Set;

public class HivemallUdfList {
    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Description.class);
        for (Class<?> clazz : annotated) {
            String s = "";

            Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                s += "[deprecated] ";
            }

            Description desc = clazz.getAnnotation(Description.class);
            s += desc.name() + " " + desc.value() + " " + desc.extended();

            System.out.println(s);
        }
    }
}
