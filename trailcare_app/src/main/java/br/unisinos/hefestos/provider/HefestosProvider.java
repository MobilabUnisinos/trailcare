package br.unisinos.hefestos.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import br.unisinos.hefestos.utility.LogUtils;

public class HefestosProvider extends ContentProvider {

    private static final String TAG = LogUtils.makeLogTag(HefestosProvider.class);
    private HefestosDbHelper mOpenHelper;
    private HefestosProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new HefestosDbHelper(getContext());
        mUriMatcher = new HefestosProviderUriMatcher();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        HefestosUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        switch (matchingUriEnum) {
            case PTRAIL_RESOURCE:
                retCursor = listPtrails(projection, selection, selectionArgs, sortOrder);
                break;
            case RESOURCE_TAG:
                retCursor = findResourceByTagCode(projection, selection, selectionArgs, sortOrder);
                break;
            default:
                final SelectionBuilder builder = buildExpandedSelection(uri, matchingUriEnum.code);

                retCursor = builder
                        .where(selection, selectionArgs)
                        .query(db, false, projection, sortOrder, null);
                break;
        }

        Context context = getContext();
        if ((null != context) && (null != retCursor)) {
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return retCursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        HefestosUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        return matchingUriEnum.contentType;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long _id = 0;
        Uri returnUri = null;

        Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        HefestosUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        if (matchingUriEnum.table != null) {
            _id = db.insertOrThrow(matchingUriEnum.table, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (_id > 0) {
            switch (matchingUriEnum) {
                case RESOURCE: {
                    returnUri = HefestosContract.Resource.buildResourceUri(_id);
                    break;
                }
                case PTRAIL: {
                    returnUri = HefestosContract.PTrail.buildResourceUri(_id);
                    break;
                }
                case USER:{
                    returnUri = HefestosContract.User.buildResourceUri(_id);
                    break;
                }
                case TAG:{
                    returnUri = HefestosContract.Tag.buildResourceUri(_id);
                    break;
                }
            }
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG, "delete(uri=" + uri + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);

        int retVal = builder.where(selection, selectionArgs).delete(db);

        if (retVal != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final SelectionBuilder builder = buildSimpleSelection(uri);

        int retVal = builder.where(selection, selectionArgs).update(db, values);

        if (retVal != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return retVal;
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        HefestosUriEnum matchingUriEnum = mUriMatcher.matchCode(match);
        if (matchingUriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (matchingUriEnum) {
            case RESOURCE: {
                builder.table(HefestosContract.Tables.RESOURCE);
                break;
            }
            case PTRAIL:{
                builder.table(HefestosContract.Tables.PTRAIL);
                break;
            }
            case USER:{
                builder.table(HefestosContract.Tables.USER);
                break;
            }
            case TAG:{
                builder.table(HefestosContract.Tables.TAG);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return builder;
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        HefestosUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        // The main Uris, corresponding to the root of each type of Uri, do not have any selection
        // criteria so the full table is used. The others apply a selection criteria.
        switch (matchingUriEnum) {
            case PTRAIL:
            case RESOURCE:
            case USER:
            case TAG:
                return builder.table(matchingUriEnum.table);
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }
    
    private Cursor listPtrails(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder sRestaurantByCoordinatesQueryBuilder = new SQLiteQueryBuilder();

        StringBuilder builder = new StringBuilder();
        builder.append(HefestosContract.Tables.PTRAIL);
        builder.append(" INNER JOIN ");
        builder.append(HefestosContract.Tables.RESOURCE);
        builder.append(" ON ");
        builder.append(HefestosContract.Tables.PTRAIL);
        builder.append(".");
        builder.append(HefestosContract.PTrail.RESOURCE_ID);
        builder.append(" = ");
        builder.append(HefestosContract.Tables.RESOURCE);
        builder.append(".");
        builder.append(HefestosContract.Resource._ID);
        builder.append(" INNER JOIN ");
        builder.append(HefestosContract.Tables.USER);
        builder.append(" ON ");
        builder.append(HefestosContract.Tables.USER);
        builder.append(".");
        builder.append(HefestosContract.User._ID);
        builder.append(" = ");
        builder.append(HefestosContract.Tables.PTRAIL);
        builder.append(".");
        builder.append(HefestosContract.PTrail.USER_ID);

        sRestaurantByCoordinatesQueryBuilder.setTables(builder.toString());

        return sRestaurantByCoordinatesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor findResourceByTagCode(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        StringBuilder builder = new StringBuilder();
        builder.append(HefestosContract.Tables.TAG);
        builder.append(" INNER JOIN ");
        builder.append(HefestosContract.Tables.RESOURCE);
        builder.append(" ON ");
        builder.append(HefestosContract.Tables.TAG);
        builder.append(".");
        builder.append(HefestosContract.Tag.RESOURCE_ID);
        builder.append(" = ");
        builder.append(HefestosContract.Tables.RESOURCE);
        builder.append(".");
        builder.append(HefestosContract.Resource._ID);

        sqLiteQueryBuilder.setTables(builder.toString());

        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }
}
