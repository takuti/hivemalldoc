import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Hivemalldoc {
    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Description.class);

        Map<String, Set<String>> packages = new TreeMap<>();

        for (Class<?> clazz : annotated) {
            Description desc = clazz.getAnnotation(Description.class);
            String s = "- " + desc.name();

            Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                s += " **[deprecated]**";
            }
            s += "\n  ";

            s += desc.value().replaceAll("_FUNC_\\(([^)]*)\\)", "```sql\n  " + desc.name() + "($1)\n  ```\n  ");

            if (!desc.extended().isEmpty()) {
                s += "\n  - " + desc.extended();
            }

            String packageName = clazz.getPackage().getName();
            packages.computeIfAbsent(packageName, k -> new TreeSet<>());
            Set<String> descList = packages.get(packageName);
            descList.add(s);
        }

        for (Map.Entry<String, Set<String>> e : packages.entrySet()) {
            System.out.println("### " + e.getKey() + "\n");
            for (String desc : e.getValue()) {
                System.out.println(desc);
            }
            System.out.println();
        }
    }
}
