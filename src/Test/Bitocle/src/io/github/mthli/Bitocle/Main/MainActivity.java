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
import android.widget.AutoCompleteTextView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Login.LoginActivity;
import io.github.mthli.Bitocle.R;

public class MainActivity extends FragmentActivity {
    private MainFragment fragment;

    private AutoCompleteTextView search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setHomeButtonEnabled(false);

        fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);

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
            case R.id.main_menu_about:
                /* Do something */
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
                                str = str.toLowerCase() + ".css";
                                editor.putString(getString(R.string.login_sp_highlight_css), str);
                                editor.commit();
                                dialog.dismiss();
                            }
                        }
                );
        dialog = builder.create();
        dialog.show();
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
