package io.github.mthli.Bitocle.WebView;

public class GetImage {
    public static String getImageUrl(String base64, String imageType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("data:image/");
        stringBuilder.append(imageType);
        stringBuilder.append(";base64,");
        stringBuilder.append(base64);

        return stringBuilder.toString();
    }

    public static String getImage(String imageUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>\n");
        stringBuilder.append("<body style=\"background-color:#dddddd;margin:auto\">\n");
        stringBuilder.append("<span class=\"border:solid 1px #333333;\">\n");
        stringBuilder.append("<img src=\"");
        stringBuilder.append(imageUrl);
        stringBuilder.append("\" style=\"\"/>\n");
        stringBuilder.append("</span>\n");
        stringBuilder.append("</body>\n");
        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }
}
