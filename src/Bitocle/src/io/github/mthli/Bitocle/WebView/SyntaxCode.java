package io.github.mthli.Bitocle.WebView;

public class SyntaxCode {
    public static final String BASE_URL = "file:///android_asset/highlight/";

    public static String syntaxCode(String content) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<!DOCTYPE html>\n");
        stringBuilder.append("<html>\n");
        stringBuilder.append("<head>\n");

        stringBuilder.append("<link rel=\"stylesheet\" href=\"styles/github.css\"></link>\n");

        stringBuilder.append("<script src=\"highlight.pack.js\"></script>\n");
        stringBuilder.append("<script>hljs.initHighlightingOnLoad();</script>\n");
        stringBuilder.append("</head>\n");
        stringBuilder.append("<body>\n");

        // stringBuilder.append("<pre style=\"word-wrap:break-word\"><code>");
        stringBuilder.append("<pre><code>");

        stringBuilder.append(content.replaceAll("<", "&lt;"));

        stringBuilder.append("</code></pre>\n");
        stringBuilder.append("</body>\n");
        stringBuilder.append("</html>\n");

        return stringBuilder.toString();
    }
}
