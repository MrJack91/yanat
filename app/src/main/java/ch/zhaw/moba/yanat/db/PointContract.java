package ch.zhaw.moba.yanat.db;

/**
 * Created by michael on 05.03.16.
 */
public class PointContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PointContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class PointEntry implements ContractInterface {
        public static final String TABLE_NAME = "point";
        public static final String COLUMN_NAME_PROJECT_ID = "project_id";
        public static final String COLUMN_NAME_REFERENCE_ID = "reference_id";   // reference to point (default: null)
        public static final String COLUMN_NAME_GROUP_ID = "group_id";           // group num (default: own id) (1 group per absolute point (points with no reference_id)) -> used for color per group
        public static final String COLUMN_NAME_IS_GROUND_FLOOR = "is_ground_floor";
        public static final String COLUMN_NAME_POS_X = "pos_x";
        public static final String COLUMN_NAME_POS_Y = "pos_y";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_COMMENT = "comment";

    }


}
