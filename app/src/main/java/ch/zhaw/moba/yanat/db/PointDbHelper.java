package ch.zhaw.moba.yanat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ch.zhaw.moba.yanat.domain.model.Point;

/**
 * Created by michael on 05.03.16.
 */

public class PointDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "Point.db";

    private static final String COMMA_SEP = ",";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_REAL = " REAL";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PointContract.PointEntry.TABLE_NAME + " (" +
                    PointContract.PointEntry.COLUMN_NAME_ID + TYPE_INTEGER + " PRIMARY KEY" + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_CREATE_DATE + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_TSTAMP + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_PROJECT_ID + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_REFERENCE_ID + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_GROUP_ID + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_IS_ABSOLUTE + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_IS_GROUND_FLOOR + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_POS_X + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_POS_Y + TYPE_INTEGER + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_HEIGHT + TYPE_REAL + COMMA_SEP +
                    PointContract.PointEntry.COLUMN_NAME_COMMENT + TYPE_TEXT +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PointContract.PointEntry.TABLE_NAME;

    public PointDbHelper(Context context) {
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
