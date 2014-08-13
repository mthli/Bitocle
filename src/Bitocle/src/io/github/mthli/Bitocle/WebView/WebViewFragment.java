package io.github.mthli.Bitocle.WebView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.devspark.progressfragment.ProgressFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.client.GitHubClient;

public class WebViewFragment extends ProgressFragment {
    private String repoOwner;
    private String repoName;
    private String fileName;
    private String sha;

    private GitHubClient gitHubClient;

    private WebView webView;

    private WebViewTask webViewTask;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.webview_fragment);
        setContentShown(true);

        View view = getContentView();
        webView = (WebView) view.findViewById(R.id.webview_fragment);
        /* Do something */
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS.NORMAL);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.login_sp), Context.MODE_PRIVATE);
        String oAuth = sharedPreferences.getString(getString(R.string.login_sp_oauth), null);

        gitHubClient = new GitHubClient();
        gitHubClient.setOAuth2Token(oAuth);

        Intent intent = getActivity().getIntent();
        repoOwner = intent.getStringExtra(getString(R.string.content_intent_repoowner));
        repoName = intent.getStringExtra(getString(R.string.content_intent_reponame));
        fileName = intent.getStringExtra(getString(R.string.content_intent_filename));
        sha = intent.getStringExtra(getString(R.string.content_intent_sha));

        webViewTask = new WebViewTask(WebViewFragment.this);
        webViewTask.execute();
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSha() {
        return sha;
    }

    public GitHubClient getGitHubClient() {
        return gitHubClient;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebViewTask(WebViewTask webViewTask) {
        this.webViewTask = webViewTask;
    }

    public void cancelAllTasks() {
        if (webViewTask != null && webViewTask.getStatus() == AsyncTask.Status.RUNNING) {
            webViewTask.cancel(true);
        }
    }
}
