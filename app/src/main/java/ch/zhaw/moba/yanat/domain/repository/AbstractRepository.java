package ch.zhaw.moba.yanat.domain.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import ch.zhaw.moba.yanat.db.ContractInterface;
import ch.zhaw.moba.yanat.domain.model.AbstractModel;

/**
 * Created by michael on 05.03.16.
 */
abstract public class AbstractRepository<Model extends AbstractModel, Contract extends ContractInterface> {

    protected Context context = null;

    protected SQLiteOpenHelper mDbHelper = null;
    protected SQLiteDatabase dbWrite = null;

    protected ContentValues buildContentValues(Model entity) {
        // refresh tstamp
        entity.setCurrentTstamp();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_NAME_CREATE_DATE, entity.getCreateDate());
        values.put(Contract.COLUMN_NAME_TSTAMP, entity.getTstamp());
        values.put(Contract.COLUMN_NAME_DELETED, 0);

        return values;
    };

    public AbstractRepository(Context context) {
        this.context = context;
    }

    public List<Model> findAll() {
        return this.find("", null);
    }

    public List<Model> findById(int id) {
        return this.find(
                Contract.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(id)}
        );
    }

    public long add(Model entity) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(entity);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbWrite.insert(
                Contract.TABLE_NAME,
                null,
                values);
        entity.setId((int) newRowId);

        return newRowId;
    }

    public boolean update(Model entity) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(entity);

        this.dbWrite.update(
                Contract.TABLE_NAME,
                values,
                Contract.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(entity.getId())}
        );
        return true;
    }

    public boolean delete(Model entity) {
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_NAME_DELETED, 1);

        Log.v("YANAT", Contract.TABLE_NAME + ": " + Contract.COLUMN_NAME_ID + ": " + String.valueOf(entity.getId()));

        this.dbWrite.update(
                Contract.TABLE_NAME,
                values,
                Contract.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(entity.getId())}
        );
        return true;
    }

    abstract public List<Model> find(String whereFilter, String[] whereValues);

}
