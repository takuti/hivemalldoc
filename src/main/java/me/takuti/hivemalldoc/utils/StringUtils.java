package me.takuti.hivemalldoc.utils;

public class StringUtils {
    private static final String TAB = "  ";

    public static String indent(final String s) {
        return TAB + s.replaceAll("(\\r\\n|\\r|\\n)", "$1" + TAB);
    }

    public static void clear(final StringBuilder sb) {
        sb.setLength(0);
    }
}
