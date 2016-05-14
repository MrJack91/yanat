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

    protected String tableName = "";

    public AbstractRepository(Context context, String tableName) {
        this.context = context;
        this.tableName = tableName;
    }

    protected ContentValues buildContentValues(Model entity) {
        // refresh tstamp
        entity.setCurrentTstamp();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_NAME_CREATE_DATE, entity.getCreateDate());
        values.put(Contract.COLUMN_NAME_TSTAMP, entity.getTstamp());
        values.put(Contract.COLUMN_NAME_DELETED, 0);

        return values;
    }

    public List<Model> findAll() {
        return this.find("", null, null, null);
    }

    public Model findById(int id) {
        Model entity = null;
        List<Model> entities = this.find(
                Contract.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(id)},
                null,
                null
        );
        if (entities.size() > 0) {
            entity = entities.get(0);
        }
        return entity;
    }

    public long add(Model entity) {
        preDatabaseChangeHook(entity);
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = this.buildContentValues(entity);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = dbWrite.insert(
                this.tableName,
                null,
                values);
        entity.setId((int) newRowId);
        this.dbWrite.close();

        return newRowId;
    }

    public long update(Model entity) {
        preDatabaseChangeHook(entity);
        if (entity.getId() == 0) {
            this.add(entity);
        } else {
            this.dbWrite = this.mDbHelper.getWritableDatabase();
            ContentValues values = this.buildContentValues(entity);

            this.dbWrite.update(
                    this.tableName,
                    values,
                    Contract.COLUMN_NAME_ID + " LIKE ?",
                    new String[]{String.valueOf(entity.getId())}
            );
            this.dbWrite.close();
        }
        return entity.getId();
    }

    public boolean delete(Model entity) {
        preDatabaseChangeHook(entity);
        this.dbWrite = this.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_NAME_DELETED, 1);

        Log.v("YANAT", this.tableName + ": " + Contract.COLUMN_NAME_ID + ": " + String.valueOf(entity.getId()));

        this.dbWrite.update(
                this.tableName,
                values,
                Contract.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(entity.getId())}
        );
        this.dbWrite.close();
        return true;
    }

    protected void preDatabaseChangeHook(Model entity) {

    }

    abstract public List<Model> find(String whereFilter, String[] whereValues, String sortOrder, String[] select);

}
