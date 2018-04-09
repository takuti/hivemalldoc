package me.takuti.hivemalldoc;

import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hivemalldoc {
    private static final String TAB = "  ";

    public static void main(String... args) {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Description.class);

        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> packages = new TreeMap<>();

        Pattern func = Pattern.compile("_FUNC_\\((.*?)\\)");

        for (Class<?> annotatedClass : annotatedClasses) {
            Description description = annotatedClass.getAnnotation(Description.class);

            sb.append("- ").append(description.name());

            Deprecated deprecated = annotatedClass.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                sb.append(" ").append("**[deprecated]**");
            }

            sb.append("\n");

            String value = description.value();
            Matcher matcher = func.matcher(value);
            if (matcher.find()) {
                value = matcher.replaceAll("```sql\n" + description.name() + "($1)\n" + "```\n");
            }
            sb.append(indent(value));
            sb.append("\n");

            if (!description.extended().isEmpty()) {
                sb.append(indent("- ")).append(description.extended());
            }

            String packageName = annotatedClass.getPackage().getName();
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

    private static String indent(final String s) {
        return TAB + s.replaceAll("(\\r\\n|\\r|\\n)", "$1" + TAB);
    }

    private static void clear(final StringBuilder sb) {
        sb.setLength(0);
    }
}
