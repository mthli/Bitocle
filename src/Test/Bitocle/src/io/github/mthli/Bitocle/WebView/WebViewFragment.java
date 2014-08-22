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
    private String owner;
    private String name;
    private String filename;
    private String sha;

    private GitHubClient client;

    private WebView webView;

    private WebViewTask task;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.webview_fragment);
        setContentEmpty(false);
        setContentShown(true);

        View view = getContentView();
        webView = (WebView) view.findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS.NORMAL);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDisplayZoomControls(false);

        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.login_sp), Context.MODE_PRIVATE);
        String OAuth = preferences.getString(getString(R.string.login_sp_oauth), null);

        client = new GitHubClient();
        client.setOAuth2Token(OAuth);

        Intent intent = getActivity().getIntent();
        owner = intent.getStringExtra(getString(R.string.webview_intent_owner));
        name = intent.getStringExtra(getString(R.string.webview_intent_name));
        sha = intent.getStringExtra(getString(R.string.webview_intent_sha));
        filename = intent.getStringExtra(getString(R.string.webview_intent_title));

        task = new WebViewTask(WebViewFragment.this);
        task.execute();
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getSha() {
        return sha;
    }

    public String getFilename() {
        return filename;
    }

    public GitHubClient getClient() {
        return client;
    }

    public WebView getWebView() {
        return webView;
    }

    public WebViewTask getTask() {
        return task;
    }
    public void setTask(WebViewTask task) {
        this.task = task;
    }

    public void allTaskDown() {
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }
}
