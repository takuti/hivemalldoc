package me.takuti.hivemalldoc.utils;

public class MarkdownUtils {

    public static String asBold(final String s) {
        return "**" + s + "**";
    }

    public static String asListElement(final String s) {
        return "- " + s;
    }

    public static String asCodeBlock(final String s) {
        return asCodeBlock(s, "");
    }

    public static String asCodeBlock(final String s, final String lang) {
        return "```" + lang + "\n" + s + "\n```\n";
    }

    public static String asHeader(final String s, int level) {
        return StringUtils.repeat("#", level) + " " + s + "\n";
    }
}
