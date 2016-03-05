package ch.zhaw.moba.yanat.db;

import android.provider.BaseColumns;

/**
 * Created by michael on 05.03.16.
 */
public class ProjectContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProjectContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ProjectEntry implements BaseColumns {
        public static final String TABLE_NAME = "project";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CREATE_DATE = "create_date";
        public static final String COLUMN_NAME_TSTAMP = "timestamp";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PDF = "pdf";
        public static final String COLUMN_NAME_PDF_WIDTH = "pdf_width";
        public static final String COLUMN_NAME_PDF_HEIGHT = "pdf_height";

    }


}
