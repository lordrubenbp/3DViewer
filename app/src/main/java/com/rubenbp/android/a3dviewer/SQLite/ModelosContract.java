package com.rubenbp.android.a3dviewer.SQLite;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ruben on 21/12/2017.
 */

public class ModelosContract {


    private ModelosContract() {}

    public static final String CONTENT_AUTHORITY = "com.rubenbp.android.a3dviewer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MODELS = "models";

    public static final class ModelEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MODELS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MODELS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MODELS;

        public final static String TABLE_NAME = "models";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_MODEL_NAME ="name";

        public final static String COLUMN_MODEL_EXTENSION = "extension";

        public final static String COLUMN_MODEL_ANIMATION = "animation";

        public final static String COLUMN_MODEL_DOWNLOAD_ID = "downloadID";

        public final static String COLUMN_MODEL_SIZE = "size";

        public final static String COLUMN_MODEL_PATH = "path";

    }

}


