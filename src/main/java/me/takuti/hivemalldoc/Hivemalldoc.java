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

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class Hivemalldoc {

    public static void main(String... args) {
        Map<String, Set<String>> packages = getHivemallPerPackageDocumentSet();

        for (Map.Entry<String, Set<String>> e : packages.entrySet()) {
            System.out.println(MarkdownUtils.asHeader(e.getKey(), 3));
            for (String desc : e.getValue()) {
                System.out.println(desc);
            }
        }
    }

    private static Map<String, Set<String>> getHivemallPerPackageDocumentSet() {
        Reflections reflections = new Reflections("hivemall");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Description.class);

        StringBuilder sb = new StringBuilder();
        Map<String, Set<String>> packages = new TreeMap<>();

        Pattern func = Pattern.compile("_FUNC_(\\(.*?\\))(.*)", Pattern.DOTALL);

        for (Class<?> annotatedClass : annotatedClasses) {
            Deprecated deprecated = annotatedClass.getAnnotation(Deprecated.class);
            if (deprecated != null) {
                continue;
            }

            Description description = annotatedClass.getAnnotation(Description.class);

            String[] values = description.value().split("\n", 2);

            String value = values[0];
            Matcher matcher = func.matcher(value);
            if (matcher.find()) {
                value = MarkdownUtils.asInlineCode(description.name() + matcher.group(1))
                        + escapeHtml(matcher.group(2));
            }
            sb.append(MarkdownUtils.asListElement(value));

            StringBuilder sbExtended = new StringBuilder();
            if (values.length == 2) {
                sbExtended.append(values[1]);
                sb.append("\n");
            }
            if (!description.extended().isEmpty()) {
                sbExtended.append(description.extended());
                sb.append("\n");
            }

            String extended = sbExtended.toString();
            if (!extended.isEmpty()) {
                sb.append(StringUtils.indent(MarkdownUtils.asCodeBlock(extended)));
            } else {
                sb.append("\n");
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
