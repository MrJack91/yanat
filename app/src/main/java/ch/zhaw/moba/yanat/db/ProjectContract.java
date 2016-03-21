package ch.zhaw.moba.yanat.db;

/**
 * Created by michael on 05.03.16.
 */
public class ProjectContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProjectContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ProjectEntry implements ContractInterface {
        public static final String TABLE_NAME = "project";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PDF = "pdf";
        public static final String COLUMN_NAME_PDF_WIDTH = "pdf_width";
        public static final String COLUMN_NAME_PDF_HEIGHT = "pdf_height";

    }


}
