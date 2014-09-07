package io.github.mthli.Bitocle.WebView;

public class SyntaxCode {
    public static final String BASE_URL = "file:///android_asset/highlight/";

    public static String syntaxCode(String content, String css) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<!DOCTYPE html>\n");
        stringBuilder.append("<html>\n");
        stringBuilder.append("<head>\n");

        // stringBuilder.append("<link rel=\"stylesheet\" href=\"styles/github.css\"></link>\n");

        stringBuilder.append("<link rel=\"stylesheet\" href=\"styles/");
        stringBuilder.append(css);
        stringBuilder.append(".css\"></link>\n");

        stringBuilder.append("<script src=\"highlight.pack.js\"></script>\n");
        stringBuilder.append("<script>hljs.initHighlightingOnLoad();</script>\n");
        stringBuilder.append("<script type=\"text/javascript\" class=\"library\" src=\"jquery.js\"></script>\n");

        stringBuilder.append("<script>");
        stringBuilder.append("$(function(){");
        stringBuilder.append("$('pre code').each(function(){");
        stringBuilder.append("var lines = $(this).text().split('\\n').length - 2;");
        stringBuilder.append("var $numbering = $('<ul/>').addClass('pre-numbering');");
        stringBuilder.append("$(this)");
        stringBuilder.append(".addClass('has-numbering')");
        stringBuilder.append(".parent()");
        stringBuilder.append(".append($numbering);");
        stringBuilder.append("for(i=1;i<=lines;i++){");
        stringBuilder.append("$numbering.append($('<li/>').text(i));");
        stringBuilder.append("}");
        stringBuilder.append("});");
        stringBuilder.append("});");
        stringBuilder.append("</script>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");
        stringBuilder.append("<pre><code>\n");
        stringBuilder.append(content.replaceAll("<", "&lt;"));
        stringBuilder.append("</code></pre>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }
}
