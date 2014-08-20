package io.github.mthli.Bitocle.Main;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.AutoCompleteTextView;
import io.github.mthli.Bitocle.R;

public class MainActivity extends FragmentActivity {
    private MainFragment fragment;

    private AutoCompleteTextView search;
    private View line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setHomeButtonEnabled(false);

        search = (AutoCompleteTextView) findViewById(R.id.main_header_search);
        line = findViewById(R.id.main_header_line);

        fragment = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment);
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
            case R.id.main_menu_theme:
                /* Do something */
                break;
            case R.id.main_menu_about:
                /* Do something */
                break;
            case R.id.main_menu_logout:
                /* Do something */
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
                    finish();
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

    public AutoCompleteTextView getSearch() {
        return search;
    }

    public View getLine() {
        return line;
    }
}
