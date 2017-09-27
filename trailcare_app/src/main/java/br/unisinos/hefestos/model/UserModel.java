package br.unisinos.hefestos.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.unisinos.hefestos.pojo.User;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.LogUtils;

public class UserModel {

    private static String LOG_TAG = LogUtils.makeLogTag(UserModel.class);

    public static User findUser(Context context){
        Cursor cursor = context.getContentResolver().query(
                HefestosContract.User.CONTENT_URI,
                HefestosContract.User.DEFAULT_PROJECTION,
                null,
                null,
                null);

        User user = null;
        Log.v(LOG_TAG,"vai buscar usuário");
        if (cursor.moveToFirst()) {
            Log.v(LOG_TAG,"achou um usuário");
            user  = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(HefestosContract.User._ID)));
            user.setExternalId(cursor.getString(cursor.getColumnIndex(HefestosContract.User.EXTERNAL_ID)));
        }

        return user ;
    }

    public static User getCurrentUser(Context context){
        int id = context.getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE).getInt(HefestosContract.USER_ID,0);
        Log.v(LOG_TAG,"vai buscar usuário no shared pref");
        User user = null;
        if(id!=0){
            Log.v(LOG_TAG,"vai buscar usuário no shared pref - achou");
            user = new User();
            user.setId(id);
        }
        Log.v(LOG_TAG,"vai buscar usuário no shared pref, id = " + id);
        return user;
    }
}
