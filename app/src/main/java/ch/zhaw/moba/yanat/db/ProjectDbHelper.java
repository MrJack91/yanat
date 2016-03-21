package ch.zhaw.moba.yanat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by michael on 05.03.16.
 */

public class ProjectDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Project.db";

    private static final String COMMA_SEP = ",";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    // private static final String TYPE_REAL = " REAL";
    // private static final String TYPE_NUMERIC = " NUMERIC";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProjectContract.ProjectEntry.TABLE_NAME + " (" +
                    ProjectContract.ProjectEntry.COLUMN_NAME_ID + TYPE_INTEGER + " PRIMARY KEY" + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_CREATE_DATE + TYPE_INTEGER + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP + TYPE_INTEGER + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_DELETED + TYPE_INTEGER + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_TITLE + TYPE_TEXT + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_PDF + TYPE_TEXT + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_PDF_WIDTH + TYPE_INTEGER + COMMA_SEP +
                    ProjectContract.ProjectEntry.COLUMN_NAME_PDF_HEIGHT + TYPE_INTEGER +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProjectContract.ProjectEntry.TABLE_NAME;

    public ProjectDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
