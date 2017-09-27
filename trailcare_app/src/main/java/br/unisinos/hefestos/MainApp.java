package br.unisinos.hefestos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import br.unisinos.hefestos.model.PTrailModel;
import br.unisinos.hefestos.model.UserModel;
import br.unisinos.hefestos.pojo.User;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.LogUtils;

public class MainApp extends Application {

    private static final String LOG_TAG = LogUtils.makeLogTag(MainApp.class);
    ScheduledThreadPoolExecutor mUpdateTrailExecutor;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        if (BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this);
        }

        // Salva o usu�rio corrente no sharedPreferences
        User user = UserModel.findUser(this);
        if(user!= null){
            SharedPreferences.Editor editor = getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.putInt(HefestosContract.USER_ID, user.getId());
            editor.apply();
        }

        // Agenda a busca �s ptrails salvas no banco
        class UpdatePtrailsTask implements Runnable {

            @Override
            public void run() {
                PTrailModel.sendPtrails(mContext);
            }
        }

        mUpdateTrailExecutor = new ScheduledThreadPoolExecutor(1);

        if (BuildConfig.DEBUG){
            mUpdateTrailExecutor.scheduleWithFixedDelay(new UpdatePtrailsTask(), 1, 1, TimeUnit.MINUTES);
        }else{
            mUpdateTrailExecutor.scheduleWithFixedDelay(new UpdatePtrailsTask(), HefestosContract.HOURS_BETWEEN_TRAILS_UPDATE, HefestosContract.HOURS_BETWEEN_TRAILS_UPDATE, TimeUnit.HOURS);
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(mUpdateTrailExecutor != null){
            mUpdateTrailExecutor.shutdownNow();
        }
    }
}
