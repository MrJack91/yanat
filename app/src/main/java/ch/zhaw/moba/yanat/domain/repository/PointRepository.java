package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.moba.yanat.db.PointContract;
import ch.zhaw.moba.yanat.db.PointDbHelper;
import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.db.ProjectDbHelper;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;

/**
 * Created by michael on 05.03.16.
 */
public class PointRepository extends AbstractRepository {

    protected Context context = null;
    protected PointDbHelper mDbHelper = null;

    protected SQLiteDatabase dbWrite = null;

    /** project id for the points to load */
    protected int projectId = 0;

    public PointRepository(Context context, int projectId) {
        this.context = context;
        this.projectId = projectId;
        this.mDbHelper = new PointDbHelper(this.context);
    }

    protected ContentValues buildContentValues(Point point) {
        // refresh tstamp
        point.setCurrentTstamp();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // values.put(PointContract.PointEntry.COLUMN_NAME_ID, point.getId());
        values.put(PointContract.PointEntry.COLUMN_NAME_CREATE_DATE, point.getCreateDate());
        values.put(PointContract.PointEntry.COLUMN_NAME_TSTAMP, point.getTstamp());
        values.put(PointContract.PointEntry.COLUMN_NAME_PROJECT_ID, this.projectId);
        values.put(PointContract.PointEntry.COLUMN_NAME_REFERENCE_ID, point.getReferenceId());
        values.put(PointContract.PointEntry.COLUMN_NAME_GROUP_ID, point.getGroupId());
        values.put(PointContract.PointEntry.COLUMN_NAME_IS_ABSOLUTE, point.isAbsolute());
        values.put(PointContract.PointEntry.COLUMN_NAME_IS_GROUND_FLOOR, point.isGroundFloor());
        values.put(PointContract.PointEntry.COLUMN_NAME_POS_X, point.getPosX());
        values.put(PointContract.PointEntry.COLUMN_NAME_POS_Y, point.getPosY());
        values.put(PointContract.PointEntry.COLUMN_NAME_HEIGHT, point.getHeight());
        values.put(PointContract.PointEntry.COLUMN_NAME_COMMENT, point.getComment());

        return values;
    }

    public long add(Point point) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(point);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbWrite.insert(
                PointContract.PointEntry.TABLE_NAME,
                null,
                values);

        return newRowId;
    }

    public boolean update(Point point) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(point);

        this.dbWrite.update(
                PointContract.PointEntry.TABLE_NAME,
                values,
                PointContract.PointEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(point.getId())}
        );
        return true;
    }

    public boolean delete(Point point) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        this.dbWrite.delete(
                PointContract.PointEntry.TABLE_NAME,
                PointContract.PointEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(point.getId())}
        );
        return true;
    }

    /**
     * Gets all points for a project
     * @return
     */
    public List<Point> findAll() {
        // todo: implement point list for project

        // read db
        SQLiteDatabase dbRead = this.mDbHelper.getReadableDatabase();
        List<Point> points = new ArrayList();

        String sortOrder = PointContract.PointEntry.COLUMN_NAME_TSTAMP + " DESC";
        Cursor cursor = dbRead.query(
                PointContract.PointEntry.TABLE_NAME,      // The table to query
                null,                                     // The columns to return
                PointContract.PointEntry.COLUMN_NAME_PROJECT_ID + " LIKE ?",    // The columns for the WHERE clause
                new String[]{String.valueOf(projectId)},                        // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_ID));
            Long createDate = cursor.getLong(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_CREATE_DATE));
            Long tstamp = cursor.getLong(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_TSTAMP));
            int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_PROJECT_ID));
            int referenceId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_REFERENCE_ID));
            int groupId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_GROUP_ID));
            Short nAbsolute = cursor.getShort(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_IS_ABSOLUTE));
            Short nGroundFloor = cursor.getShort(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_IS_GROUND_FLOOR));
            int posX = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_POS_X));
            int posY = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_POS_Y));
            Float height = cursor.getFloat(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_HEIGHT));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_COMMENT));


            Point point = new Point();
            point.setId(id);
            point.setCreateDate(createDate);
            point.setTstamp(tstamp);

            point.setProjectId(projectId);
            point.setReferenceId(referenceId);
            point.setGroupId(groupId);
            point.setIsAbsolute(nAbsolute == 1);
            point.setIsGroundFloor(nGroundFloor == 1);
            point.setPosX(posX);
            point.setPosY(posY);
            point.setHeight(height);
            point.setComment(comment);

            // increment title name (a,b,c,...,z,aa,ab,ac,...)
            point.setTitle("[A]");


            // add point to collection
            points.add(point);

            cursor.moveToNext();
        }

        return points;
    }

}
