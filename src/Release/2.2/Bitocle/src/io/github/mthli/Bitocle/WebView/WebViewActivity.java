package io.github.mthli.Bitocle.WebView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import io.github.mthli.Bitocle.R;

public class WebViewActivity extends FragmentActivity {
    private WebViewFragment fragment;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean change = false;

    private WindowManager.LayoutParams layoutParams;
    private static final float BRIGHTNESS_NIGHT = 0.01f;

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

        preferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        editor = preferences.edit();

        layoutParams = getWindow().getAttributes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = WebViewActivity.this.getMenuInflater();
        inflater.inflate(R.menu.webview_menu, menu);

        MenuItem hor = menu.findItem(R.id.webview_menu_horizontal);
        MenuItem night = menu.findItem(R.id.webview_menu_night);
        String str = preferences.getString(getString(R.string.login_sp_horizontal), "false");
        if (str.equals("true")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            hor.setChecked(true);
        }
        str = preferences.getString(getString(R.string.login_sp_night), "false");
        if (str.equals("true")) {
            layoutParams.screenBrightness = BRIGHTNESS_NIGHT;
            getWindow().setAttributes(layoutParams);
            night.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                fragment.allTaskDown();
                if (change) {
                    setResult(1);
                } else {
                    setResult(0);
                }
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
            case R.id.webview_menu_horizontal:
                change = true;
                if (menuItem.isChecked()) {
                    editor.putString(getString(R.string.login_sp_horizontal), "false");
                    editor.commit();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    menuItem.setChecked(false);
                } else {
                    editor.putString(getString(R.string.login_sp_horizontal), "true");
                    editor.commit();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    menuItem.setChecked(true);
                }
                break;
            case R.id.webview_menu_night:
                change = true;
                if (menuItem.isChecked()) {
                    editor.putString(getString(R.string.login_sp_night), "false");
                    editor.commit();
                    layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                    getWindow().setAttributes(layoutParams);
                    menuItem.setChecked(false);
                } else {
                    editor.putString(getString(R.string.login_sp_night), "true");
                    editor.commit();
                    layoutParams.screenBrightness = BRIGHTNESS_NIGHT;
                    getWindow().setAttributes(layoutParams);
                    menuItem.setChecked(true);
                }
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
            if (change) {
                setResult(1);
            } else {
                setResult(0);
            }
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

                                    String filename = fragment.getFilename();
                                    if (!MimeType.isImage(filename) && !MimeType.isMarkdown(filename)) {
                                        task = new WebViewTask(fragment);
                                        fragment.setTask(task);
                                        task.execute();
                                    }
                                }

                                dialog.dismiss();
                            }
                        }
                );
        dialog = builder.create();
        dialog.show();
    }
}
