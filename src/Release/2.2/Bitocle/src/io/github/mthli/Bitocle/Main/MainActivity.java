package io.github.mthli.Bitocle.Main;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Login.LoginActivity;
import io.github.mthli.Bitocle.R;
import io.github.mthli.Bitocle.WebView.StyleMarkdown;
import org.apache.commons.io.IOUtils;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.StarService;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends FragmentActivity {
    private MainFragment fragment;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private WindowManager.LayoutParams layoutParams;
    private static final float BRIGHTNESS_NIGHT = 0.01f;

    private AutoCompleteTextView search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);

        preferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        editor = preferences.edit();

        layoutParams = getWindow().getAttributes();

        search = (AutoCompleteTextView) findViewById(R.id.main_header_search);
        View line = findViewById(R.id.main_header_line);
        fragment.setSearch(search);
        fragment.setLine(line);
    }

    @Override
    protected void onPause() {
        search.clearFocus();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = MainActivity.this.getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem star = menu.findItem(R.id.main_menu_star);
        MenuItem bookmark = menu.findItem(R.id.main_menu_bookmark);
        MenuItem about = menu.findItem(R.id.main_menu_about);
        MenuItem logout = menu.findItem(R.id.main_menu_logout);
        fragment.setStar(star);
        fragment.setBookmark(bookmark);
        fragment.setAbout(about);
        fragment.setLogout(logout);

        MenuItem hor = menu.findItem(R.id.main_menu_horizontal);
        MenuItem night = menu.findItem(R.id.main_menu_night);
        fragment.setHor(hor);
        fragment.setNight(night);

        String str = preferences.getString(getString(R.string.login_sp_horizontal), "false");
        if (str.equals("true")) {
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
                fragment.changeToRepo(Flag.REPO_SECOND);
                break;
            case R.id.main_menu_star:
                fragment.changeToStar(true);
                break;
            case R.id.main_menu_bookmark:
                fragment.changeToBookmark();
                break;
            case R.id.main_menu_refresh:
                fragment.refreshAction();
                break;
            case R.id.main_menu_highlight:
                showHighlightDialog();
                break;
            case R.id.main_menu_horizontal:
                if (menuItem.isChecked()) {
                    editor.putString(getString(R.string.login_sp_horizontal), "false");
                    editor.commit();
                    menuItem.setChecked(false);
                } else {
                    editor.putString(getString(R.string.login_sp_horizontal), "true");
                    editor.commit();
                    menuItem.setChecked(true);
                }
                break;
            case R.id.main_menu_night:
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
            case R.id.main_menu_about:
                showAboutDialog();
                break;
            case R.id.main_menu_logout:
                logoutAction();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (fragment.getCurrentId()) {
                case MainFragment.REPO_ID:
                    fragment.allTaskDown();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(fragment);
                    transaction.commit();
                    finish();
                    System.exit(0);
                    break;
                case MainFragment.STAR_ID:
                    fragment.changeToRepo(Flag.REPO_SECOND);
                    break;
                case MainFragment.BOOKMARK_ID:
                    fragment.backFromBookmark();
                    break;
                case MainFragment.REPO_CONTENT_ID:
                    fragment.backToPrevious();
                    break;
                case MainFragment.STAR_CONTENT_ID:
                    fragment.backToPrevious();
                    break;
                case MainFragment.COMMIT_ID:
                    fragment.backFromCommit();
                    break;
                default:
                    break;
            }
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

    private void showHighlightDialog() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        AlertDialog dialog;
        int num = preferences.getInt(getString(R.string.login_sp_highlight_num), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setSingleChoiceItems(
                        R.array.dialog_highlight_list,
                        num,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putInt(getString(R.string.login_sp_highlight_num), which);
                                String str = getResources().getStringArray(R.array.dialog_highlight_list)[which];
                                str = str.toLowerCase();
                                editor.putString(getString(R.string.login_sp_highlight_css), str);
                                editor.commit();
                                dialog.dismiss();
                            }
                        }
                );
        dialog = builder.create();
        dialog.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.about_label);

        String lang;
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            lang = getString(R.string.about_readme_zh);
        } else {
            lang = getString(R.string.about_readme_en);
        }

        String str = null;
        try {
            InputStream inputStream = getResources().getAssets().open(lang);
            str = IOUtils.toString(inputStream);
        } catch (IOException i) {
            /* Do nothing */
        }

        final WebView webView = new WebView(MainActivity.this);
        webView.loadDataWithBaseURL(
                StyleMarkdown.BASE_URL,
                str,
                null,
                getString(R.string.webview_encoding),
                null
        );
        webView.setVisibility(View.VISIBLE);
        builder.setView(webView);

        builder.setPositiveButton(R.string.about_button_star, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(starThread).start();
                SuperToast.create(
                        MainActivity.this,
                        getString(R.string.about_thx),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.GREEN)
                ).show();
            }
        });
        builder.setNegativeButton(R.string.about_button_close, null);
        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    Runnable starThread = new Runnable() {
        @Override
        public void run() {
            StarService starService = new StarService(fragment.getClient());
            RepositoryId repositoryId = new RepositoryId(
                    getString(R.string.about_author),
                    getString(R.string.about_name)
            );

            try {
                if (!starService.isStarring(repositoryId)) {
                    starService.star(repositoryId);
                }
            } catch (IOException i) {
                /* Do nothing */
            }
        }
    };

    private void logoutAction() {
        RAction rAction = new RAction(MainActivity.this);
        BAction bAction = new BAction(MainActivity.this);

        try {
            rAction.openDatabase(true);
            bAction.openDatabase(true);

            rAction.deleteAll();
            bAction.unMarkAll();

            rAction.closeDatabase();
            bAction.closeDatabase();
        } catch (SQLException s) {
            rAction.closeDatabase();
            bAction.closeDatabase();

            SuperToast.create(
                    MainActivity.this,
                    getString(R.string.main_logout_failed),
                    SuperToast.Duration.VERY_SHORT,
                    Style.getStyle(Style.RED)
            ).show();
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getString(R.string.login_sp_oauth));
        editor.remove(getString(R.string.login_sp_username));
        editor.remove(getString(R.string.login_sp_highlight_num));
        editor.remove(getString(R.string.login_sp_highlight_css));
        editor.commit();

        fragment.allTaskDown();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }
}
