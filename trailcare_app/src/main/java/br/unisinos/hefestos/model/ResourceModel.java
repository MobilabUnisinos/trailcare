package br.unisinos.hefestos.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.unisinos.hefestos.pojo.LimitCoordinates;
import br.unisinos.hefestos.pojo.Resource;
import br.unisinos.hefestos.pojo.Tag;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.CoordinatesUtility;
import br.unisinos.hefestos.utility.LogUtils;

public class ResourceModel {

    private static final String LOG_TAG = LogUtils.makeLogTag(ResourceModel.class);

    public static Resource findByTagCode(String tagCode, Context context){
        String strFilter = HefestosContract.Tables.TAG + "." + HefestosContract.Tag.CODE + " = ? ";
        String[] selectionArgs = new String[] {
                tagCode
        };

        Cursor cReview = context.getContentResolver().query(
                HefestosContract.ResourceTag.CONTENT_URI,
                HefestosContract.ResourceTag.DEFAULT_PROJECTION,
                strFilter,
                selectionArgs,
                null);

        return buildResource(cReview);
    }

    public static Resource findByCoordinates(Double latitude, Double longitude, Context context) {

        StringBuilder whereClause = new StringBuilder(
                HefestosContract.Resource.LATITUDE).append(" >= ?")
                .append(" AND ")
                .append(HefestosContract.Resource.LATITUDE).append(" <= ?")
                .append(" AND ")
                .append(HefestosContract.Resource.LONGITUDE).append(" >= ?")
                .append(" AND ")
                .append(HefestosContract.Resource.LONGITUDE).append(" <= ?");

        LimitCoordinates limitCoordinates = CoordinatesUtility.getLimitCoordinates(latitude,longitude,HefestosContract.MIN_DISTANCE_BETWEEN_METERS);

        Double correctionFactor = CoordinatesUtility.getCorrectionFactor(latitude);

        String[] selectionArgs  = new String[]{
                String.valueOf(limitCoordinates.getMinLatitude()),
                String.valueOf(limitCoordinates.getMaxLatitude()),
                String.valueOf(limitCoordinates.getMinLongitude()),
                String.valueOf(limitCoordinates.getMaxLongitude()),
                String.valueOf(latitude),
                String.valueOf(latitude),
                String.valueOf(longitude),
                String.valueOf(longitude),
                String.valueOf(correctionFactor)
        };

        StringBuilder orderByBuilder = new StringBuilder();
        orderByBuilder
                .append(HefestosContract.Resource.LATITUDE)
                .append(" is null, ((? - ")
                .append(HefestosContract.Resource.LATITUDE)
                .append(") * ( ? -")
                .append(HefestosContract.Resource.LATITUDE)
                .append(") +  ( ? - ")
                .append(HefestosContract.Resource.LONGITUDE)
                .append(") * ( ? - ")
                .append(HefestosContract.Resource.LONGITUDE)
                .append(") * ?)");

        Cursor cursor = context.getContentResolver().query(
                HefestosContract.Resource.CONTENT_URI,
                HefestosContract.Resource.DEFAULT_PROJECTION,
                whereClause.toString(),
                selectionArgs,
                orderByBuilder.toString());

        return buildResource(cursor);
    }
    
    private static Resource buildResource(Cursor cursor){
        Resource resource = null;

        if (cursor.moveToFirst()) {
            resource = new Resource();
            resource.setId(cursor.getInt(cursor.getColumnIndex(HefestosContract.Resource.ID_ALIAS)));
            resource.setType(cursor.getInt(cursor.getColumnIndex(HefestosContract.Resource.TYPE)));
            resource.setDescription(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.DESCRIPTION)));
            resource.setLatitude(cursor.getDouble(cursor.getColumnIndex(HefestosContract.Resource.LATITUDE)));
            resource.setLongitude(cursor.getDouble(cursor.getColumnIndex(HefestosContract.Resource.LONGITUDE)));
            resource.setName(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.NAME)));
            resource.setExternalId(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.EXTERNAL_ID)));

            int tagIdIndex = cursor.getColumnIndex(HefestosContract.Tag.ID_ALIAS);
            if(tagIdIndex!= -1){
                Tag tag = new Tag();
                tag.setId(cursor.getInt(tagIdIndex));
                tag.setExternalId(cursor.getString(cursor.getColumnIndex(HefestosContract.Tag.EXTERNAL_ID)));
                tag.setCode(cursor.getString(cursor.getColumnIndex(HefestosContract.Tag.CODE)));
                List<Tag> tags = new ArrayList<>();
                tags.add(tag);
                resource.setTags(tags);
            }
        }

        Log.v(LOG_TAG, "preenchimento de resource, achou alguma coisa? "+ resource == null ? "nao" : "sim" );

        return resource;
    }
}
