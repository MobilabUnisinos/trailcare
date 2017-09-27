package br.unisinos.hefestos.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.unisinos.hefestos.pojo.PTrail;
import br.unisinos.hefestos.pojo.Resource;
import br.unisinos.hefestos.pojo.User;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.LogUtils;
import br.unisinos.hefestos.webservice.Connection;
import br.unisinos.hefestos.webservice.Webservice;

public class PTrailModel {

    private static final String LOG_TAG = LogUtils.makeLogTag(PTrailModel.class);

    public static PTrail insert(Double latitude, Double longitude, Context context){
        Resource resource = ResourceModel.findByCoordinates(latitude,longitude,context);
        if (resource == null){
            return null;
        }
        PTrail pTrail = new PTrail(resource, new Date(),UserModel.getCurrentUser(context));
        return insert(pTrail,context);
    }

    public static PTrail insert(String tagId, Context context){
        Resource resource = ResourceModel.findByTagCode(tagId,context);
        if (resource == null){
            return null;
        }
        PTrail pTrail = new PTrail(resource, new Date(),UserModel.getCurrentUser(context));

        return insert(pTrail,context);
    }

    public static PTrail insert(PTrail pTrail, Context context) {
        ContentValues mNewValues = new ContentValues();

        mNewValues.put(HefestosContract.PTrail.RESOURCE_ID, pTrail.getResource().getId());
        mNewValues.put(HefestosContract.PTrail.DATE_TIME, pTrail.getDate().getTime());
        mNewValues.put(HefestosContract.PTrail.USER_ID, pTrail.getUser().getId());

        Uri uri = context.getContentResolver().insert(
                HefestosContract.PTrail.CONTENT_URI,
                mNewValues);

        Log.v(LOG_TAG, "inseriu ptrail? " + uri == null? "não":"sim");

        if(uri == null) {
            return null;
        }

        long id = ContentUris.parseId(uri);
        pTrail.setId(Long.valueOf(id).intValue());

        Log.v(LOG_TAG, "inseriu ptrail? id " + id);

        return pTrail;
    }

    public static List<PTrail> list(Context context){
        List<PTrail> pTrails = null;

        StringBuilder orderBy = new StringBuilder()
                .append(HefestosContract.PTrail.DATE_TIME)
                .append(" ASC");

        Cursor cursor = context.getContentResolver().query(
                HefestosContract.PtrailResource.CONTENT_URI,
                HefestosContract.PtrailResource.DEFAULT_PROJECTION,
                null,
                null,
                orderBy.toString());

        if (cursor.moveToFirst()) {
            pTrails = new ArrayList<>();
            do{
                Resource resource = new Resource();
                resource.setId(cursor.getInt(cursor.getColumnIndex(HefestosContract.Resource.ID_ALIAS)));
                resource.setType(cursor.getInt(cursor.getColumnIndex(HefestosContract.Resource.TYPE)));
                resource.setDescription(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.DESCRIPTION)));
                resource.setLatitude(cursor.getDouble(cursor.getColumnIndex(HefestosContract.Resource.LATITUDE)));
                resource.setLongitude(cursor.getDouble(cursor.getColumnIndex(HefestosContract.Resource.LONGITUDE)));
                resource.setName(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.NAME)));
                resource.setExternalId(cursor.getString(cursor.getColumnIndex(HefestosContract.Resource.EXTERNAL_ID)));

                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(HefestosContract.User.ID_ALIAS)));
                user.setExternalId(cursor.getString(cursor.getColumnIndex(HefestosContract.User.EXTERNAL_ID_ALIAS)));

                PTrail pTrail = new PTrail();
                pTrail.setId(cursor.getInt(cursor.getColumnIndex(HefestosContract.PTrail.ID_ALIAS)));
                pTrail.setDate(new Date(cursor.getLong(cursor.getColumnIndex(HefestosContract.PTrail.DATE_TIME))));
                pTrail.setResource(resource);
                pTrail.setUser(user);

                pTrails.add(pTrail);

            }while (cursor.moveToNext());
        }

        return pTrails;
    }

    public static boolean delete(PTrail pTrail, Context context){

        StringBuilder strFilter = new StringBuilder()
                .append(HefestosContract.Tables.PTRAIL)
                .append(".")
                .append(HefestosContract.PTrail._ID)
                .append("= ?");

        String[] selectionArgs = new String[] {
                String.valueOf(pTrail.getId())
        };

        int reviewDelete = context.getContentResolver().delete(
                HefestosContract.PTrail.CONTENT_URI,
                strFilter.toString(),
                selectionArgs);

        if(reviewDelete == 1){
            return true;
        }
        return false;
    }

    public static boolean delete(List<PTrail> pTrails, Context context){
        StringBuilder strFilter = new StringBuilder()
                .append(HefestosContract.Tables.PTRAIL)
                .append(".")
                .append(HefestosContract.PTrail._ID)
                .append(" IN (")
                .append(TextUtils.join(",", Collections.nCopies(pTrails.size(),"?")))
                .append(")");

        String[] selectionArgs = new String[pTrails.size()];

        int i = 0;
        for (PTrail pTrail:pTrails) {
            selectionArgs[i] = String.valueOf(pTrail.getId());
            i++;
        }

        int reviewDelete = context.getContentResolver().delete(
                HefestosContract.PTrail.CONTENT_URI,
                strFilter.toString(),
                selectionArgs);

        if(reviewDelete == pTrails.size()){
            return true;
        }

        return false;
    }

    public static void sendPtrails(Context context){

        Log.d(LOG_TAG,"entrou em sendPtrails");

        List<PTrail> pTrails = list(context);
        if(pTrails == null || pTrails.isEmpty()){
            Log.d(LOG_TAG,"não achou ptrails para enviar");
        }

        Log.d(LOG_TAG,"achou ptrails para enviar, tamanho = " + pTrails.size());

        String webserviceResponse = Connection.callWebservice(Webservice.sendPtrails(pTrails));
        if(webserviceResponse == null || webserviceResponse.trim().isEmpty()){
            Log.d(LOG_TAG,"falha no retorno do ws");
        }else{
            boolean retorno = delete(pTrails,context);
            Log.d(LOG_TAG,"conseguiu apagar todas as ptrails? " + retorno);
        }

    }
}
