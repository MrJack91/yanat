package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
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

    /** used for title, required manually reset */
    protected int titleOffset = 0;

    public PointRepository(Context context, int projectId) {
        super(context, PointContract.PointEntry.TABLE_NAME);
        this.mDbHelper = new PointDbHelper(this.context);
        this.projectId = projectId;
    }

    protected ContentValues buildContentValues(Point point) {
        ContentValues values = super.buildContentValues(point);
        values.put(PointContract.PointEntry.COLUMN_NAME_PROJECT_ID, this.projectId);
        values.put(PointContract.PointEntry.COLUMN_NAME_REFERENCE_ID, point.getReferenceId());
        values.put(PointContract.PointEntry.COLUMN_NAME_GROUP_ID, point.getGroupId());
        values.put(PointContract.PointEntry.COLUMN_NAME_IS_GROUND_FLOOR, point.isGroundFloor());
        values.put(PointContract.PointEntry.COLUMN_NAME_POS_X, point.getPosX());
        values.put(PointContract.PointEntry.COLUMN_NAME_POS_Y, point.getPosY());
        values.put(PointContract.PointEntry.COLUMN_NAME_HEIGHT, point.getHeight());
        values.put(PointContract.PointEntry.COLUMN_NAME_COMMENT, point.getComment());

        return values;
    }

    /**
     * Gets all points for a project
     * If you use public, reset titleOffset manually to 0
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

        String sortOrder = PointContract.PointEntry.COLUMN_NAME_CREATE_DATE + " ASC";
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
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_ID));
            Long createDate = cursor.getLong(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_CREATE_DATE));
            Long tstamp = cursor.getLong(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_TSTAMP));
            int projectId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_PROJECT_ID));
            int referenceId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_REFERENCE_ID));
            int groupId = cursor.getInt(cursor.getColumnIndexOrThrow(PointContract.PointEntry.COLUMN_NAME_GROUP_ID));
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
            point.setIsGroundFloor(nGroundFloor == 1);
            point.setPosX(posX);
            point.setPosY(posY);
            point.setHeight(height);
            point.setComment(comment);

            point.setTitle(this.makeTitle(this.titleOffset));

            // add point to collection
            points.add(point);

            cursor.moveToNext();
        }
        cursor.close();

        return points;
    }

    protected String makeTitle(int offset) {
        String title;
        // todo: add more chars than 26 (aa,ab,ac,...)

        if (offset > 26) {
            offset = 26;
        }
        title = String.valueOf(Character.toChars(65+offset));

        this.titleOffset++;
        return title;
    }


    public List<Point> findAll() {
        // load all fix points (root points without references)
        this.titleOffset = 0;
        List<Point> points = new ArrayList();
        points.addAll(this.findChildren(null, null, 0));

        // search groundfloor to calc relative groundfloor height
        List<Point> floorGroundPoints = this.find("is_ground_floor = 1", null);
        if (floorGroundPoints.size() == 1) {
            Point groundFloorPoint = this.findLoadedById(floorGroundPoints.get(0).getId(), points);
            for (Point point : points) {
                point.setHeightToGroundFloor((point.getHeightAbsolute() - groundFloorPoint.getHeightAbsolute()));
            }
        }
        return points;
    }

    /**
     * Loads all childs recursiver
     * @param parentPoint
     * @return
     */
    protected List<Point> findChildren(Point parentPoint, Point rootPoint, int groupId) {
        int parentId = 0;
        if (parentPoint != null) {
            parentId = parentPoint.getId();
        }

        List<Point> points = this.find("reference_id = " + parentId, null);
        List<Point> childPoints = new ArrayList();
        for (Point point : points) {
            // calc heights of this point
            if (parentPoint == null) {
                // is absolute point
                rootPoint = point;
                point.setHeightAbsolute(point.getHeight());
                point.setHeightRelative(0f);
                groupId++;
            } else {
                // is relative point
                point.setHeightAbsolute(parentPoint.getHeightAbsolute() + point.getHeight());
                point.setHeightRelative(point.getHeightAbsolute() - rootPoint.getHeightAbsolute());
            }
            point.setGroupId(groupId);

            // calc all childs
            childPoints.addAll(this.findChildren(point, rootPoint, groupId));
        }
        points.addAll(childPoints);

        return points;
    }

    public Point findLoadedById(int id, List<Point> points) {
        for (Point point : points) {
            if (point.getId() == id) {
                return point;
            }
        }
        return null;
    }

    /**
     * Group points by coordinates -> get duplicates -> points on same location
     * @param points
     * @return
     */
    public List<List> groupPointsByCoordinates(List<Point> points) {
        // sort by coordinates to easy recognize same coordinates
        Collections.sort(points);

        /*
        for (Point point : points) {
            Log.v("YANAT group before: ", point.toString());
        }
        */

        // List<List<Point>> groupedPoints = new ArrayList<List<Point>>;
        List<List> groupedPoints = new ArrayList();
        int lastX;
        int lastY;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            List<Point> groupOfPoints = new ArrayList();
            groupOfPoints.add(point);
            lastX = point.getPosX();
            lastY = point.getPosY();

            int j;
            for (j = i+1; j < points.size(); j++) {
                Point nextPoint = points.get(j);
                if (lastX != nextPoint.getPosX() || lastY != nextPoint.getPosY()) {
                    break;
                }
                groupOfPoints.add(nextPoint);
            }
            i = j-1;

            groupedPoints.add(groupOfPoints);
        }

        /*
        for (List<Point> groupOfPoints : groupedPoints) {
            for (Point point : groupOfPoints) {
                Log.v("YANAT group after: ", point.toString());
            }
        }
        */

        return groupedPoints;
    }

}
