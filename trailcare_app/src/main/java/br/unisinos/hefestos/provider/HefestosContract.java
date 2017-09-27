package br.unisinos.hefestos.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class HefestosContract {

    public static final String SHARED_PREFERENCES = "br.unisinos.hefestos.shared_preference";
    public static final String LAST_LATITUDE = "last_latitude";
    public static final String LAST_LONGITUDE = "last_longitude";
    public static final String USER_ID = "user_id";
    public static final int MIN_DISTANCE_BETWEEN_METERS = 10;
    public static final int HOURS_BETWEEN_TRAILS_UPDATE = 1;

    // SQL commands
    public static final String CREATE_TABLE = " CREATE TABLE ";
    public static final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    public static final String TYPE_TEXT = " TEXT ";
    public static final String TYPE_INTEGER = " INTEGER ";
    public static final String TYPE_REAL = " REAL ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String NULL = " NULL ";
    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";


    public static final String CONTENT_TYPE_APP_BASE = "hefestos.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;


    public static final String CONTENT_AUTHORITY = "br.unisinos.hefestos.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RESOURCE = "resource";

    public static final String PATH_PTRAIL = "ptrail";

    public static final String PATH_USER = "user";

    public static final String PATH_TAG = "tag";

    public static final String PATH_PTRAIL_RESOURCE = "ptrail_resource";
    public static final String PATH_RESOURCE_TAG = "resource_tag";

    public interface Tables {

        String USER = "user";
        String RESOURCE = "resource";
        String PTRAIL = "ptrail";
        String TAG = "tag";
    }

    interface References {
        String RESOURCE_ID = "REFERENCES " + Tables.RESOURCE + "(" + Resource._ID + ")";
        String USER_ID = "REFERENCES " + Tables.USER + "(" + User._ID + ")";
    }

    interface UserColumns{
        /**
         User identifier from external system
         */
        String EXTERNAL_ID = "external_id";

    }

    interface TagColumns{
        /**
         Tag identifier from external system
         */
        String EXTERNAL_ID = "external_id";

        /**
         * Tag's code
         */
        String CODE = "code";

        /**
         * Resource's id
         */
        String RESOURCE_ID = "resource_id";
    }

    interface PTrailColumns{
        /**
         * Resource's id
         */
        String RESOURCE_ID = "resource_id";

        /**
         * date and time of ocurrence
         */
        String DATE_TIME = "date_time";

        /**
         * User who owns this trail
         */
        String USER_ID = "user_id";

    }

    interface ResourceColumns {

        /**
         * Resource type (indoor or outdoor)
         */
        String TYPE = "type";
        /**
         * Resource name.
         */
        String NAME = "name";
        /**
         * Resource description.
         */
        String DESCRIPTION = "description";
        /**
         * Resource latitude, if outdoor.
         */
        String LATITUDE = "latitude";
        /**
         * Resource longitude, if outdoor.
         */
        String LONGITUDE = "longitude";

        /**
        Resource identifier from external system
         */
        String EXTERNAL_ID = "external_id";

    }

    public static class Tag implements TagColumns, BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAG;

        public static final String CONTENT_TYPE_ID = "tag";

        public static final String ID_ALIAS = Tables.TAG + "_" + Tag._ID;
        public static final String EXTERNAL_ID_ALIAS = Tables.TAG + "_" + Tag.EXTERNAL_ID;

        public static Uri buildResourceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class User implements UserColumns,BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String CONTENT_TYPE_ID = "user";

        public static final String ID_ALIAS = Tables.USER + "_" + User._ID;

        public static final String EXTERNAL_ID_ALIAS = Tables.USER + "_" + User.EXTERNAL_ID;

        public static final String[] DEFAULT_PROJECTION = {
                Tables.USER + "." + User._ID,
                Tables.USER + "." + User.EXTERNAL_ID
        };

        public static Uri buildResourceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class Resource implements ResourceColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESOURCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESOURCE;

        public static final String CONTENT_TYPE_ID = "resource";

        public static final String ID_ALIAS = Tables.RESOURCE + "_" + Resource._ID;

        public static final String[] DEFAULT_PROJECTION = {
                Tables.RESOURCE + "." + Resource._ID  + " " + Resource.ID_ALIAS,
                Tables.RESOURCE + "." + Resource.TYPE,
                Tables.RESOURCE + "." + Resource.NAME,
                Tables.RESOURCE + "." + Resource.DESCRIPTION,
                Tables.RESOURCE + "." + Resource.LATITUDE,
                Tables.RESOURCE + "." + Resource.LONGITUDE,
                Tables.RESOURCE + "." + Resource.EXTERNAL_ID
        };

        public static Uri buildResourceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class PTrail implements PTrailColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PTRAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PTRAIL;

        public static final String CONTENT_TYPE_ID = "ptrail";

        public static final String ID_ALIAS = Tables.PTRAIL + "_" + PTrail._ID;

        public static Uri buildResourceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class PtrailResource {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PTRAIL_RESOURCE).build();

        public static final String[] DEFAULT_PROJECTION = {
                Tables.RESOURCE + "." + Resource._ID + " " + Resource.ID_ALIAS,
                Tables.RESOURCE + "." + Resource.TYPE,
                Tables.RESOURCE + "." + Resource.NAME,
                Tables.RESOURCE + "." + Resource.DESCRIPTION,
                Tables.RESOURCE + "." + Resource.LATITUDE,
                Tables.RESOURCE + "." + Resource.LONGITUDE,
                Tables.RESOURCE + "." + Resource.EXTERNAL_ID,
                Tables.PTRAIL + "." + PTrail._ID + " " + PTrail.ID_ALIAS,
                Tables.PTRAIL + "." + PTrail.RESOURCE_ID,
                Tables.PTRAIL + "." + PTrail.DATE_TIME,
                Tables.PTRAIL + "." + PTrail.USER_ID,
                Tables.USER + "." + User._ID + " " + User.ID_ALIAS,
                Tables.USER+ "." + User.EXTERNAL_ID  + " " + User.EXTERNAL_ID_ALIAS,

        };

    }

    public static class ResourceTag {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESOURCE_TAG).build();

        public static final String[] DEFAULT_PROJECTION = {
                Tables.RESOURCE + "." + Resource._ID + " " + Resource.ID_ALIAS,
                Tables.RESOURCE + "." + Resource.TYPE,
                Tables.RESOURCE + "." + Resource.NAME,
                Tables.RESOURCE + "." + Resource.DESCRIPTION,
                Tables.RESOURCE + "." + Resource.LATITUDE,
                Tables.RESOURCE + "." + Resource.LONGITUDE,
                Tables.RESOURCE + "." + Resource.EXTERNAL_ID,
                Tables.TAG + "." + Tag._ID + " " + Tag.ID_ALIAS,
                Tables.TAG+ "." + Tag.EXTERNAL_ID + " " + Tag.EXTERNAL_ID_ALIAS,
                Tables.TAG+ "." + Tag.CODE

        };

    }


    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        }
        return null;
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        }
        return null;
    }
}
