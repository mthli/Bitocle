package io.github.mthli.Bitocle.WebView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import io.github.mthli.Bitocle.R;

public class WebViewActivity extends FragmentActivity {
    private WebViewFragment fragment;

    private WebViewTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.webview_intent_title));
        String subTitle = intent.getStringExtra(getString(R.string.webview_intent_subtitle));

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(title);
        actionBar.setSubtitle(subTitle);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragment = (WebViewFragment) getSupportFragmentManager().findFragmentById(R.id.webview_fragment);
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
                fragment.allTaskDown();
                finish();
                break;
            case R.id.webview_menu_refresh:
                task = fragment.getTask();
                if (task != null && task.getStatus() == AsyncTask.Status.FINISHED) {
                    task = new WebViewTask(fragment);
                    fragment.setTask(task);
                    task.execute();
                }
                break;
            case R.id.webview_menu_highlight:
                showHighlightDialog();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fragment.allTaskDown();
            finish();
        }

        return true;
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

    private void showHighlightDialog() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        AlertDialog dialog;
        int num = preferences.getInt(getString(R.string.login_sp_highlight_num), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this)
                .setSingleChoiceItems(
                        R.array.dialog_highlight_list,
                        num,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task = fragment.getTask();
                                if (task != null && task.getStatus() == AsyncTask.Status.FINISHED) {
                                    editor.putInt(getString(R.string.login_sp_highlight_num), which);
                                    String str = getResources().getStringArray(R.array.dialog_highlight_list)[which];
                                    str = str.toLowerCase();
                                    editor.putString(getString(R.string.login_sp_highlight_css), str);
                                    editor.commit();

                                    task = new WebViewTask(fragment);
                                    fragment.setTask(task);
                                    task.execute();
                                }

                                dialog.dismiss();
                            }
                        }
                );
        dialog = builder.create();
        dialog.show();
    }
}
