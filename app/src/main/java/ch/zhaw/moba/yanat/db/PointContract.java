package ch.zhaw.moba.yanat.db;

import android.provider.BaseColumns;

/**
 * Created by michael on 05.03.16.
 */
public class PointContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PointContract() {}

    /* Inner class that defines the table contents */
    public static abstract class PointEntry implements BaseColumns {
        public static final String TABLE_NAME = "point";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CREATE_DATE = "create_date";
        // public static final String COLUMN_NAME_TSTAMP = "timestamp";
        public static final String COLUMN_NAME_REFERENCE_ID = "reference_id";
        public static final String COLUMN_NAME_GROUP_ID = "group_id";
        public static final String COLUMN_NAME_IS_ABSOLUTE = "is_absolute";
        public static final String COLUMN_NAME_IS_GROUND_FLOOR = "is_ground_floor";
        public static final String COLUMN_NAME_POS_X = "pos_x";
        public static final String COLUMN_NAME_POS_Y = "pos_y";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_COMMENT = "comment";








    }


}
