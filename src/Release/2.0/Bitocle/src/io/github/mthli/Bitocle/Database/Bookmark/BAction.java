package io.github.mthli.Bitocle.Database.Bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class BAction {
    private BHelper BHelper;
    private SQLiteDatabase sqLiteDatabase;

    public BAction(Context context) {
        BHelper = new BHelper(context);
    }

    public void openDatabase(boolean rw) throws SQLException {
        if (rw) {
            sqLiteDatabase = BHelper.getWritableDatabase();
        } else {
            sqLiteDatabase = BHelper.getReadableDatabase();
        }
    }

    public void closeDatabase() {
        BHelper.close();
    }

    public boolean checkBookmark(String sha) {
        Cursor cursor = sqLiteDatabase.query(
                Bookmark.TABLE,
                new String[] {Bookmark.SHA},
                Bookmark.SHA + "=?",
                new String[] {sha},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            return result;
        }
        return false;
    }

    public void addBookmark(Bookmark bookmark) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Bookmark.TITLE, bookmark.getTitle());
        contentValues.put(Bookmark.DATE, bookmark.getDate());
        contentValues.put(Bookmark.TYPE, bookmark.getType());
        contentValues.put(Bookmark.REPO_OWNER, bookmark.getRepoOwner());
        contentValues.put(Bookmark.REPO_NAME, bookmark.getRepoName());
        contentValues.put(Bookmark.REPO_PATH, bookmark.getRepoPath());
        contentValues.put(Bookmark.SHA, bookmark.getSha());
        contentValues.put(Bookmark.KEY, bookmark.getKey());
        sqLiteDatabase.insert(Bookmark.TABLE, null, contentValues);
    }

    public void unMarkBySha(String sha) {
        sqLiteDatabase.execSQL("DELETE FROM " + Bookmark.TABLE + " WHERE SHA like \"" + sha + "\"");
    }

    public void unMarkByKey(String key) {
        sqLiteDatabase.execSQL("DELETE FROM " + Bookmark.TABLE + " WHERE KEY like \"" + key + "\"");
    }

    public void unMarkAll() {
        sqLiteDatabase.execSQL("DELETE FROM " + Bookmark.TABLE);
    }

    public List<Bookmark> listBookmarks() {
        List<Bookmark> marks = new ArrayList<Bookmark>();
        Cursor cursor = sqLiteDatabase.query(
                Bookmark.TABLE,
                new String[] {
                        Bookmark.TITLE,
                        Bookmark.DATE,
                        Bookmark.TYPE,
                        Bookmark.REPO_OWNER,
                        Bookmark.REPO_NAME,
                        Bookmark.REPO_PATH,
                        Bookmark.SHA,
                        Bookmark.KEY
                },
                null,
                null,
                null,
                null,
                Bookmark.TITLE
        );

        if (cursor == null) {
            return marks;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Bookmark mark = readBookMark(cursor);
            marks.add(mark);
            cursor.moveToNext();
        }
        cursor.close();

        return marks;
    }

    private Bookmark readBookMark(Cursor cursor) {
        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(cursor.getString(0));
        bookmark.setDate(cursor.getString(1));
        bookmark.setType(cursor.getString(2));
        bookmark.setRepoOwner(cursor.getString(3));
        bookmark.setRepoName(cursor.getString(4));
        bookmark.setRepoPath(cursor.getString(5));
        bookmark.setSha(cursor.getString(6));
        bookmark.setKey(cursor.getString(7));

        return bookmark;
    }
}
