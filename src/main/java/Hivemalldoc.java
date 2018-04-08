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

        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> packages = new TreeMap<>();

        for (Class<?> clazz : annotated) {
            Description desc = clazz.getAnnotation(Description.class);
            sb.append("- " + desc.name());

            Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                sb.append(" **[deprecated]**");
            }
            sb.append("\n  ");

            sb.append(desc.value().replaceAll("_FUNC_\\(([^)]*)\\)", "```sql\n  " + desc.name() + "($1)\n  ```\n  "));

            if (!desc.extended().isEmpty()) {
                sb.append("\n  - " + desc.extended());
            }

            String packageName = clazz.getPackage().getName();
            packages.computeIfAbsent(packageName, k -> new TreeSet<>());
            Set<String> descList = packages.get(packageName);
            descList.add(sb.toString());

            clear(sb);
        }

        for (Map.Entry<String, Set<String>> e : packages.entrySet()) {
            System.out.println("### " + e.getKey() + "\n");
            for (String desc : e.getValue()) {
                System.out.println(desc);
            }
            System.out.println();
        }
    }

    private static void clear(final StringBuilder sb) {
        sb.setLength(0);
    }
}
