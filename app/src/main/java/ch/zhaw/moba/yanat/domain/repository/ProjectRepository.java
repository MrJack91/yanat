package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.db.ProjectDbHelper;
import ch.zhaw.moba.yanat.domain.model.Project;

/**
 * Created by michael on 05.03.16.
 */
public class ProjectRepository extends AbstractRepository {

    protected Context context = null;
    protected ProjectDbHelper mDbHelper = null;

    protected SQLiteDatabase dbWrite = null;


    public ProjectRepository(Context context) {
        this.context = context;
        this.mDbHelper = new ProjectDbHelper(this.context);
    }

    protected ContentValues buildContentValues(Project project) {
        // refresh tstamp
        project.setCurrentTstamp();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // values.put(ProjectContract.ProjectEntry.COLUMN_NAME_ID, project.getId());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_CREATE_DATE, project.getCreateDate());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP, project.getTstamp());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE, project.getTitle());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_PDF, project.getPdf());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_PDF_WIDTH, project.getPdfWidth());
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_PDF_HEIGHT, project.getPdfHeight());
        return values;
    }

    public long add(Project project) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(project);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbWrite.insert(
                ProjectContract.ProjectEntry.TABLE_NAME,
                null,
                values);

        return newRowId;
    }

    public boolean update(Project project) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(project);

        this.dbWrite.update(
                ProjectContract.ProjectEntry.TABLE_NAME,
                values,
                ProjectContract.ProjectEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(project.getId())}
        );
        return true;
    }

    public boolean delete(Project project) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        this.dbWrite.delete(
                ProjectContract.ProjectEntry.TABLE_NAME,
                ProjectContract.ProjectEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(project.getId())}
        );
        return true;
    }

    /**
     * Gets all projects
     * @return
     */
    public List<Project> findAll() {
        // read db
        SQLiteDatabase dbRead = this.mDbHelper.getReadableDatabase();
        List<Project> projects = new ArrayList();

        String sortOrder = ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP + " DESC";
        Cursor cursor = dbRead.query(
                ProjectContract.ProjectEntry.TABLE_NAME,  // The table to query
                null,                                     // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_ID));
            Long createDate = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_CREATE_DATE));
            Long tstamp = cursor.getLong(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE));
            String pdf = cursor.getString(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_PDF));
            int pdfWidth = cursor.getInt(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_PDF_WIDTH));
            int pdfHeight = cursor.getInt(cursor.getColumnIndexOrThrow(ProjectContract.ProjectEntry.COLUMN_NAME_PDF_HEIGHT));

            Project project = new Project();
            project.setId(id);
            project.setCreateDate(createDate);
            project.setTstamp(tstamp);

            project.setTitle(title);
            project.setPdf(pdf);
            project.setPdfWidth(pdfWidth);
            project.setPdfHeight(pdfHeight);

            // add project to collection
            projects.add(project);

            cursor.moveToNext();
        }

        return projects;
    }

}
