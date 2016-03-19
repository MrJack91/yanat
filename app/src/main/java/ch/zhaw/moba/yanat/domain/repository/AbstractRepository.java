package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;

import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;

/**
 * Created by michael on 05.03.16.
 */
abstract public class AbstractRepository {

    // todo: implement methods with generics??

    // abstract public List<Project> find(String whereFilter, String[] whereValues);

    // abstract public boolean delete(Point element);

    /*
    public List<Project> findAll() {
        return this.find(null, null);
    }
    */


    // abstract public List<Project> findById(int projectId);
    /*
    return this.find(
                ProjectContract.ProjectEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(projectId)}
        );
     */

}
