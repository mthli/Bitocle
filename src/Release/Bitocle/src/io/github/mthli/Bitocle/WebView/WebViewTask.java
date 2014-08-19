package io.github.mthli.Bitocle.WebView;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.util.EncodingUtils;

import java.io.IOException;

public class WebViewTask extends AsyncTask<Void, Integer, Boolean> {
    private WebViewFragment webViewFragment;
    private Context context;

    private GitHubClient gitHubClient;
    private String repoOwner;
    private String repoName;
    private String fileName;
    private String sha;

    private WebView webView;
    private String content;
    private String image;

    public WebViewTask(WebViewFragment webViewFragment) {
        this.webViewFragment = webViewFragment;
    }

    @Override
    protected void onPreExecute() {
        webViewFragment.setContentShown(false);

        context = webViewFragment.getContentView().getContext();

        gitHubClient = webViewFragment.getGitHubClient();
        repoOwner = webViewFragment.getRepoOwner();
        repoName = webViewFragment.getRepoName();
        fileName = webViewFragment.getFileName();
        sha = webViewFragment.getSha();

        webView = webViewFragment.getWebView();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        RepositoryId repositoryId = RepositoryId.create(repoOwner, repoName);
        DataService dataService = new DataService(gitHubClient);
        MarkdownService markdownService = new MarkdownService(gitHubClient);

        Blob blob;
        try {
            blob = dataService.getBlob(repositoryId, sha);
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        String base64 = blob.getContent();
        if (!MimeType.isImage(fileName)) {
            byte[] bytes = EncodingUtils.fromBase64(base64);
            content = new String(bytes);
        }

        if (MimeType.isImage(fileName)) {
            String imageUrl = GetImage.getImageUrl(base64, MimeType.getImageExtension(fileName));
            image = GetImage.getImage(imageUrl);
        } else if (MimeType.isMarkdown(fileName)) {
            try {
                content = markdownService.getHtml(content, MarkdownService.MODE_GFM);
                content = StyleMarkdown.styleMarkdown(content);
            } catch (IOException i) {
                return false;
            }
        } else {
            content = SyntaxCode.syntaxCode(content);
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (MimeType.isImage(fileName)) {
                webView.loadDataWithBaseURL(
                        null,
                        image,
                        null,
                        context.getString(R.string.webview_encoding),
                        null
                );
            } else if (MimeType.isMarkdown(fileName)) {
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
            webViewFragment.setContentEmpty(false);
            webView.setVisibility(View.VISIBLE);
            webViewFragment.setContentShown(true);
        } else {
            webView.setVisibility(View.INVISIBLE);
            webViewFragment.setEmptyText(R.string.webview_empty_get_data_failed);
            webViewFragment.setContentEmpty(true);
            webViewFragment.setContentShown(true);
        }
    }
}
