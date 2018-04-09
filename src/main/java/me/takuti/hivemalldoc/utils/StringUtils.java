package me.takuti.hivemalldoc.utils;

public class StringUtils {
    private static final String TAB = "  ";

    public static String indent(final String s) {
        return TAB + s.replaceAll("(\\r\\n|\\r|\\n)", "$1" + TAB);
    }

    public static String repeat(final String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void clear(final StringBuilder sb) {
        sb.setLength(0);
    }
}
