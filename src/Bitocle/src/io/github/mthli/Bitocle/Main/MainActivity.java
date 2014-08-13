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
import android.support.v4.view.MenuItemCompat;
import android.view.*;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;
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
    private MainFragment mainFragment;

    public static SearchView searchView;
    public static MenuItem searchItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setHomeButtonEnabled(false);

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = MainActivity.this.getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        searchItem = menu.findItem(R.id.main_menu_add);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.main_menu_add_hint));
        searchItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                mainFragment.getActionMenu().close(true);
                if (mainFragment.getSearchViewStatus()) {
                    mainFragment.searchViewDown();
                } else {
                    if (mainFragment.CURRENT_ID != MainFragment.REPO_ID) {
                        mainFragment.changeToRepo();
                    }
                }
                break;
            case R.id.main_menu_bookmark:
                mainFragment.getActionMenu().close(true);
                mainFragment.changeToBookmark();
                break;
            case R.id.main_menu_about:
                mainFragment.getActionMenu().close(true);
                aboutDialogShow();
                break;
            case R.id.main_menu_logout:
                mainFragment.getActionMenu().close(true);
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
            mainFragment.getActionMenu().close(true);

            if (mainFragment.getSearchViewStatus()) {
                mainFragment.searchViewDown();
            } else {
                switch (mainFragment.CURRENT_ID) {
                    case MainFragment.REPO_ID:
                        mainFragment.cancelAllTasks();
                        MainActivity.this.finish();
                        break;
                    case MainFragment.BOOKMARK_ID:
                        if (mainFragment.getContentItemListBuffer().size() > 0) {
                            mainFragment.changeToContent();
                        } else {
                            mainFragment.changeToRepo();
                        }
                        break;
                    case MainFragment.CONTENT_ID:
                        if (mainFragment.getContentItemListBuffer().size() > 1) {
                            mainFragment.backToParent();
                        } else {
                            mainFragment.changeToRepo();
                        }
                        break;
                    case MainFragment.HISTORY_ID:
                        System.out.println("***: " + mainFragment.getContentItemListBuffer().size());
                        if (mainFragment.getContentItemListBuffer().size() > 0) {
                            mainFragment.changeToContent();
                        } else {
                            mainFragment.changeToRepo();
                        }
                        break;
                    case MainFragment.COMMIT_ID:
                        mainFragment.changeToRepo();
                        break;
                    default:
                        break;
                }
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

            Toast.makeText(
                    MainActivity.this,
                    R.string.main_logout_failed,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.login_sp), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.login_sp_username));
        editor.remove(getString(R.string.login_sp_oauth));
        editor.commit();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        mainFragment.cancelAllTasks();
        MainActivity.this.finish();
    }

    private void aboutDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.main_about_label);

        String str = null;
        try {
            InputStream inputStream = getResources().getAssets().open(getString(R.string.main_about_readme));
            str = IOUtils.toString(inputStream);
        } catch (IOException i) {
            /* Do nothing */
        }

        WebView webView = new WebView(MainActivity.this);
        webView.loadDataWithBaseURL(
                StyleMarkdown.BASE_URL,
                str,
                null,
                getString(R.string.webview_encoding),
                null
        );
        webView.setVisibility(View.VISIBLE);
        builder.setView(webView);

        builder.setPositiveButton(R.string.main_about_star, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(starThread).start();
                Toast.makeText(
                        MainActivity.this,
                        R.string.main_about_star_thx,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        builder.setNegativeButton(R.string.main_about_close, null);
        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    Runnable starThread = new Runnable() {
        @Override
        public void run() {
            StarService starService = new StarService(mainFragment.getGitHubClient());
            RepositoryId repositoryId = new RepositoryId(
                    getString(R.string.main_about_author),
                    getString(R.string.main_about_repo)
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
}
