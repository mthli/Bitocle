package io.github.mthli.Bitocle.WebView;

public class StyleMarkdown {
    public static final String BASE_URL = "file:///android_asset/markdown/";

    public static String styleMarkdown(String content) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<!DOCTYPE html>\n");
        stringBuilder.append("<html>\n");
        stringBuilder.append("<meta charset=\"utf-8\">\n");

        stringBuilder.append("<link rel=\"stylesheet\" href=\"markdown.css\">\n");
        stringBuilder.append("<style>\n");
        stringBuilder.append(".markdown-body {\n");
        stringBuilder.append("min-width: 200px;\n");
        stringBuilder.append("max-width: 790px;\n");
        stringBuilder.append("margin: 0 auto;\n");
        stringBuilder.append("padding: 30px;\n");
        stringBuilder.append("}\n");
        stringBuilder.append("</style>\n");
        stringBuilder.append("<article class=\"markdown-body\">");

        stringBuilder.append(content); //

        stringBuilder.append("</article>");

        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }
}
