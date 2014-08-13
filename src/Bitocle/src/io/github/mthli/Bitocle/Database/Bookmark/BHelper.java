package io.github.mthli.Bitocle.Database.Bookmark;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BOOKMARK.db";
    private static final int DATABASE_VERSION = 1;

    public BHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Bookmark.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /* Do nothing */
    }
}
