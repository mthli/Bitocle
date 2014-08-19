package io.github.mthli.Bitocle.WebView;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import io.github.mthli.Bitocle.R;

public class WebViewActivity extends FragmentActivity {
    private WebViewFragment webViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        String titleName = getIntent().getStringExtra(getString(R.string.content_intent_filename));
        String titlePath = getIntent().getStringExtra(getString(R.string.content_intent_filepath));

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(titleName);
        actionBar.setSubtitle(titlePath);
        actionBar.setDisplayHomeAsUpEnabled(true);

        webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.webview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = WebViewActivity.this.getMenuInflater();
        inflater.inflate(R.menu.webview_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                webViewFragment.cancelAllTasks();
                WebViewActivity.this.finish();
                break;
            case R.id.webview_menu_refresh:
                webViewFragment.cancelAllTasks();
                WebViewTask webViewTask = new WebViewTask(webViewFragment);
                webViewFragment.setWebViewTask(webViewTask);
                webViewTask.execute();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webViewFragment.cancelAllTasks();
            WebViewActivity.this.finish();
        }

        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE) {
            /* Do nothing */
        }
        else{
            /* Do nothing */
        }
    }
}
