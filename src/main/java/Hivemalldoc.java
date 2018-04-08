import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hivemalldoc {
    private static final String INDENT = "  ";

    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Description.class);

        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> packages = new TreeMap<>();

        Pattern usage = Pattern.compile("_FUNC_\\((.*?)\\)");

        for (Class<?> clazz : annotated) {
            Description desc = clazz.getAnnotation(Description.class);

            sb.append("- ").append(desc.name());
            Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                sb.append(" ").append("**[deprecated]**");
            }
            sb.append("\n");

            sb.append(INDENT);
            Matcher matcher = usage.matcher(desc.value());
            if (matcher.find()) {
                sb.append(matcher.replaceAll("```sql\n" + INDENT + desc.name() + "($1)\n" + INDENT + "```\n" + INDENT));
            } else {
                sb.append(desc.value());
            }
            sb.append("\n");

            if (!desc.extended().isEmpty()) {
                sb.append(INDENT).append("- ").append(desc.extended());
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
