package io.github.mthli.Bitocle.WebView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.util.EncodingUtils;

import java.io.IOException;

public class WebViewTask extends AsyncTask<Void, Integer, Boolean> {
    private WebViewFragment fragment;
    private Context context;

    private String owner;
    private String name;
    private String sha;
    private String filename;

    private WebView webView;
    private String css;
    private String content;
    private String image;

    public WebViewTask(WebViewFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        context = fragment.getContentView().getContext();

        owner = fragment.getOwner();
        name = fragment.getName();
        sha = fragment.getSha();
        filename = fragment.getFilename();

        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.login_sp),
                Context.MODE_PRIVATE
        );
        css = preferences.getString(
                context.getString(R.string.login_sp_highlight_css),
                context.getString(R.string.webview_default_css)
        );

        webView = fragment.getWebView();

        fragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        GitHubClient client = fragment.getClient();
        RepositoryId id = RepositoryId.create(owner, name);
        DataService dataService = new DataService(client);
        MarkdownService markdownService = new MarkdownService(client);

        Blob blob;
        try {
            blob = dataService.getBlob(id, sha);
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        String base64 = blob.getContent();
        if (!MimeType.isImage(filename)) {
            byte[] bytes = EncodingUtils.fromBase64(base64);
            content = new String(bytes);
        }

        if (MimeType.isImage(filename)) {
            String imageUrl = GetImage.getImageUrl(base64, MimeType.getImageExtension(filename));
            image = GetImage.getImage(imageUrl);
        } else if (MimeType.isMarkdown(filename)) {
            try {
                content = markdownService.getHtml(content, MarkdownService.MODE_GFM);
                content = StyleMarkdown.styleMarkdown(content);
            } catch (IOException i) {
                return false;
            }
        } else {
            content = SyntaxCode.syntaxCode(content, css);
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            SuperToast.create(
                    context,
                    context.getString(R.string.webview_wait),
                    SuperToast.Duration.VERY_SHORT,
                    Style.getStyle(Style.BLUE)
            ).show();

            String[] arr = context.getResources().getStringArray(R.array.dialog_highlight_list);
            if (css.equals(arr[0].toLowerCase())) {
                webView.setBackgroundColor(
                        context.getResources().getColor(R.color.github)
                );
            } else if (css.equals(arr[1].toLowerCase())) {
                webView.setBackgroundColor(
                        context.getResources().getColor(R.color.monokai)
                );
            } else if (css.equals(arr[2].toLowerCase())) {
                webView.setBackgroundColor(
                        context.getResources().getColor(R.color.solarized)
                );
            } else if (css.equals(arr[3].toLowerCase())) {
                webView.setBackgroundColor(
                        context.getResources().getColor(R.color.sunburst)
                );
            } else {
                webView.setBackgroundColor(
                        context.getResources().getColor(R.color.tomorrow)
                );
            }

            if (MimeType.isImage(filename)) {
                webView.loadDataWithBaseURL(
                        null,
                        image,
                        null,
                        context.getString(R.string.webview_encoding),
                        null
                );
            } else if (MimeType.isMarkdown(filename)) {
                webView.loadDataWithBaseURL(
                        StyleMarkdown.BASE_URL,
                        content,
                        null,
                        context.getString(R.string.webview_encoding),
                        null
                );
            } else {
                webView.loadDataWithBaseURL(
                        SyntaxCode.BASE_URL,
                        content,
                        null,
                        context.getString(R.string.webview_encoding),
                        null
                );
            }

            fragment.setContentEmpty(false);
            fragment.setContentShown(true);
        } else {
            webView.setVisibility(View.GONE);
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.webview_empty_error);
            fragment.setContentShown(true);
        }
    }
}
