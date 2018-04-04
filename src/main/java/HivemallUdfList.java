import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Set;

public class HivemallUdfList {
    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Description.class);
        for (Class<?> clazz : annotated) {
            Description desc = clazz.getAnnotation(Description.class);
            String s = "- " + desc.value().replaceAll("_FUNC_\\(([^)]*)\\)", "`" + desc.name() + "($1)`");
            if (!desc.extended().isEmpty()) {
                s += "<br />" + desc.extended();
            }

            Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                s += " **[deprecated]**";
            }

            System.out.println(s);
        }
    }
}
