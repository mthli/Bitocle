package io.github.mthli.Bitocle.Database.Repo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RAction {
    private RHelper rHelper;
    private SQLiteDatabase sqLiteDatabase;

    public RAction(Context context) {
        rHelper = new RHelper(context);
    }

    public void openDatabase(boolean rw) throws SQLException {
        if (rw) {
            sqLiteDatabase = rHelper.getWritableDatabase();
        } else {
            sqLiteDatabase = rHelper.getReadableDatabase();
        }
    }

    public void closeDatabase() {
        rHelper.close();
    }

    public boolean checkRepo(String git) {
        Cursor cursor = sqLiteDatabase.query(
                Repo.TABLE,
                new String[] {Repo.GIT},
                Repo.GIT + "=?",
                new String[] {git},
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

    public void addRepo(Repo repo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Repo.TITLE, repo.getTitle());
        contentValues.put(Repo.DATE, repo.getDate());
        contentValues.put(Repo.CONTENT, repo.getContent());
        contentValues.put(Repo.INFO, repo.getInfo());
        contentValues.put(Repo.OWNER, repo.getOwner());
        contentValues.put(Repo.GIT, repo.getGit());
        sqLiteDatabase.insert(Repo.TABLE, null, contentValues);
    }

    public void deleteRepo(String git) {
        sqLiteDatabase.execSQL("DELETE FROM " + Repo.TABLE + " WHERE " + Repo.GIT + " like \"" + git + "\"");
    }

    public void deleteAll() {
        sqLiteDatabase.execSQL("DELETE FROM " + Repo.TABLE);
    }

    public List<Repo> listRepos() {
        List<Repo> repoList = new ArrayList<Repo>();
        Cursor cursor = sqLiteDatabase.query(
                Repo.TABLE,
                new String[] {
                        Repo.TITLE,
                        Repo.DATE,
                        Repo.CONTENT,
                        Repo.INFO,
                        Repo.OWNER,
                        Repo.GIT
                },
                null,
                null,
                null,
                null,
                Repo.TITLE // ORDER BY
        );

        if (cursor == null) {
            return repoList;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Repo repo = readRepo(cursor);
            repoList.add(repo);
            cursor.moveToNext();
        }
        cursor.close();

        return repoList;
    }

    private Repo readRepo(Cursor cursor) {
        Repo repo = new Repo();
        repo.setTitle(cursor.getString(0));
        repo.setDate(cursor.getString(1));
        repo.setContent(cursor.getString(2));
        repo.setInfo(cursor.getString(3));
        repo.setOwner(cursor.getString(4));
        repo.setGit(cursor.getString(5));

        return repo;
    }
}
