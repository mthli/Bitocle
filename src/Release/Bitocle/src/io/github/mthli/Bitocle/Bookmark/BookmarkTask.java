package io.github.mthli.Bitocle.Bookmark;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Bitocle.Database.Bookmark.Bookmark;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.Main.RefreshType;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.RepositoryContents;

import java.util.Collections;
import java.util.List;

public class BookmarkTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private int refreshType = 0;

    private BookmarkItemAdapter bookmarkItemAdapter;
    private List<BookmarkItem> bookmarkItemList;

    public BookmarkTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.setRefreshStatus(true);
        context = mainFragment.getContentView().getContext();
        refreshType = mainFragment.getRefreshType();

        bookmarkItemAdapter = mainFragment.getBookmarkItemAdapter();
        bookmarkItemList = mainFragment.getBookmarkItemList();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        BAction bAction = new BAction(context);
        try {
            bAction.openDatabase(true);
        } catch (SQLException s) {
            bAction.closeDatabase();
            return false;
        }

        bookmarkItemList.clear();
        List<Bookmark> bookmarkList = bAction.listBookmarks();
        for (Bookmark b: bookmarkList) {
            if (b.getType().equals(RepositoryContents.TYPE_DIR)) {
                bookmarkItemList.add(
                        new BookmarkItem(
                                context.getResources().getDrawable(R.drawable.ic_type_folder),
                                b.getTitle(),
                                b.getDate(),
                                b.getType(),
                                b.getRepoOwner(),
                                b.getRepoName(),
                                b.getRepoPath(),
                                b.getSha(),
                                b.getKey()
                        )
                );
            } else {
                bookmarkItemList.add(
                        new BookmarkItem(
                                context.getResources().getDrawable(R.drawable.ic_type_file),
                                b.getTitle(),
                                b.getDate(),
                                b.getType(),
                                b.getRepoOwner(),
                                b.getRepoName(),
                                b.getRepoPath(),
                                b.getSha(),
                                b.getKey()
                        )
                );
            }
        }
        Collections.sort(bookmarkItemList);
        bAction.closeDatabase();

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (refreshType == RefreshType.BOOKMARK_FIRST) {
                if (bookmarkItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.bookmark_empty_bookmark_empty);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    bookmarkItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }
            } else {
                if (bookmarkItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.bookmark_empty_bookmark_empty);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    bookmarkItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }
                Toast.makeText(
                        context,
                        R.string.bookmark_task_successful,
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            if (refreshType == RefreshType.BOOKMARK_FIRST) {
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.bookmark_empty_get_data_failed);
                mainFragment.setContentShown(true);
            } else {
                Toast.makeText(
                        context,
                        R.string.bookmark_task_failed,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        mainFragment.setRefreshStatus(false);
    }
}