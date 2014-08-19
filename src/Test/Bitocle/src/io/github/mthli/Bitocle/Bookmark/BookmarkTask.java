package io.github.mthli.Bitocle.Bookmark;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Bookmark.Bookmark;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;

import java.util.Collections;
import java.util.List;

public class BookmarkTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;
    private Context context;

    private BookmarkItemAdapter adapter;
    private List<BookmarkItem> list;

    private List<Bookmark> bookmarks;

    public BookmarkTask(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        context = fragment.getContentView().getContext();

        adapter = fragment.getBookmarkItemAdapter();
        list = fragment.getBookmarkItemList();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        BAction action = new BAction(context);
        try {
            action.openDatabase(true);
        } catch (SQLException s) {
            action.closeDatabase();
            return false;
        }

        bookmarks = action.listBookmarks();
        Collections.sort(bookmarks);

        action.closeDatabase();

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            list.clear();
            for (Bookmark b : bookmarks) {
                list.add(
                        new BookmarkItem(
                                b.getTitle(),
                                b.getType(),
                                b.getOwner(),
                                b.getName(),
                                b.getPath(),
                                b.getSha(),
                                b.getKey()
                        )
                );
            }
            if (list.size() == 0) {
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.bookmark_empty_list);
                fragment.setContentShown(true);
            } else {
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
            }
        } else {
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.bookmark_empty_error);
            fragment.setContentShown(true);
        }
    }
}
