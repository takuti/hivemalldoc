package me.takuti.hivemalldoc;

import me.takuti.hivemalldoc.utils.MarkdownUtils;
import me.takuti.hivemalldoc.utils.StringUtils;

import org.reflections.Reflections;
import org.apache.hadoop.hive.ql.exec.Description;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hivemalldoc {

    public static void main(String... args) {
        Map<String, Set<String>> packages = getHivemallPerPackageDocumentSet();

        for (Map.Entry<String, Set<String>> e : packages.entrySet()) {
            System.out.println(MarkdownUtils.asHeader(e.getKey(), 3));
            for (String desc : e.getValue()) {
                System.out.println(desc);
            }
            System.out.println();
        }
    }

    private static Map<String, Set<String>> getHivemallPerPackageDocumentSet() {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Description.class);

        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> packages = new TreeMap<>();

        Pattern func = Pattern.compile("_FUNC_(\\(.*?\\))(.*)$");

        for (Class<?> annotatedClass : annotatedClasses) {
            Description description = annotatedClass.getAnnotation(Description.class);

            sb.append(MarkdownUtils.asListElement(description.name()));

            Deprecated deprecated = annotatedClass.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                sb.append(" ").append(MarkdownUtils.asBold("[deprecated]"));
            }

            sb.append("\n");

            String value = description.value();
            Matcher matcher = func.matcher(value);
            if (matcher.find()) {
                value = MarkdownUtils.asCodeBlock(description.name() + matcher.group(1), "sql")
                        + matcher.group(2).trim();
            }
            sb.append(StringUtils.indent(value));

            if (!description.extended().isEmpty()) {
                sb.append("\n");
                sb.append(StringUtils.indent(MarkdownUtils.asListElement(description.extended())));
            }

            String packageName = annotatedClass.getPackage().getName();
            packages.computeIfAbsent(packageName, k -> new TreeSet<>());
            Set<String> List = packages.get(packageName);
            List.add(sb.toString());

            StringUtils.clear(sb);
        }

        return packages;
    }
}
