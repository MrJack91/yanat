package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.moba.yanat.db.PointContract;
import ch.zhaw.moba.yanat.db.PointDbHelper;
import ch.zhaw.moba.yanat.domain.model.Point;

/**
 * Created by michael on 05.03.16.
 */
public class PointRepository extends AbstractRepository<Point, PointContract.PointEntry> {

    /** project id for the points to load */
    protected int projectId = 0;

    public PointRepository(Context context, int projectId) {
        super(context);
        this.mDbHelper = new PointDbHelper(this.context);
        this.projectId = projectId;
    }

    protected ContentValues buildContentValues(Point point) {
        /*
        // refresh tstamp
        point.setCurrentTstamp();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // values.put(PointContract.PointEntry.COLUMN_NAME_ID, point.getId());
        values.put(PointContract.PointEntry.COLUMN_NAME_CREATE_DATE, point.getCreateDate());
        values.put(PointContract.PointEntry.COLUMN_NAME_TSTAMP, point.getTstamp());
        values.put(PointContract.PointEntry.COLUMN_NAME_DELETED, 0);
        */
        ContentValues values = super.buildContentValues(point);
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

    /*
    public long add(Point point) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(point);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbWrite.insert(
                PointContract.PointEntry.TABLE_NAME,
                null,
                values);
        point.setId((int) newRowId);

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
    */

    /*
    public boolean delete(Point point) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PointContract.PointEntry.COLUMN_NAME_DELETED, 1);

        this.dbWrite.update(
                PointContract.PointEntry.TABLE_NAME,
                values,
                PointContract.PointEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(point.getId())}
        );
        return true;
    }

    public List<Point> findAll() {
        return this.find("", null);
    }

    public List<Point> findById(int projectId) {
        return this.find(
                PointContract.PointEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(projectId)}
        );
    }
    */

    /**
     * Gets all points for a project
     * @return
     */
    public List<Point> find(String whereFilter, String[] whereValues) {
        // read db
        SQLiteDatabase dbRead = this.mDbHelper.getReadableDatabase();
        List<Point> points = new ArrayList();

        if (whereFilter.length() > 0) {
            whereFilter = " AND (" + whereFilter + ")";
        }
        whereFilter = PointContract.PointEntry.COLUMN_NAME_DELETED + " = 0 AND " + PointContract.PointEntry.COLUMN_NAME_PROJECT_ID + " LIKE " + projectId + whereFilter;

        String sortOrder = PointContract.PointEntry.COLUMN_NAME_TSTAMP + " DESC";
        Cursor cursor = dbRead.query(
                PointContract.PointEntry.TABLE_NAME,      // The table to query
                null,                                     // The columns to return
                whereFilter,                              // The columns for the WHERE clause
                whereValues,                              // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
        int offset = 0;
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

            point.setTitle(this.makeTitle(offset));
            offset++;


            // add point to collection
            points.add(point);

            cursor.moveToNext();
        }

        return points;
    }

    protected String makeTitle(int offset) {
        String title = "";
        // todo: add more chars than 26 (aa,ab,ac,...)

        if (offset > 26) {
            offset = 26;
        }
        title = String.valueOf(Character.toChars(65+offset));
        return title;
    }

}
