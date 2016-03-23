package ch.zhaw.moba.yanat.db;

import android.provider.BaseColumns;

/**
 * Created by michael on 21.03.16.
 */
public interface ContractInterface extends BaseColumns {
    String COLUMN_NAME_ID = "id";
    String COLUMN_NAME_CREATE_DATE = "create_date";
    String COLUMN_NAME_TSTAMP = "timestamp";
    String COLUMN_NAME_DELETED = "deleted";
}
