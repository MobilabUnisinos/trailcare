package br.unisinos.hefestos.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.unisinos.hefestos.RecursosKeywordActivity;
import br.unisinos.hefestos.RecursosListaActivity;
import br.unisinos.hefestos.pojo.SOSResource;

public class FetchWebserviceSOSDataTask extends AsyncTask<String,Void,SOSResource> {
    private static final String LOG_TAG = FetchWebserviceSOSDataTask.class.getSimpleName();

    private Context mContext;
    private RecursosListaActivity.SOSResourceTaskFinishedListener mTaskFinishedListener;
    private RecursosKeywordActivity.SOSResourceTaskFinishedListener mSosTaskFinishedListener;

    public FetchWebserviceSOSDataTask(Context mContext, RecursosListaActivity.SOSResourceTaskFinishedListener mTaskFinishedListener) {
        this.mContext = mContext;
        this.mTaskFinishedListener = mTaskFinishedListener;
    }

    public FetchWebserviceSOSDataTask(Context mContext, RecursosKeywordActivity.SOSResourceTaskFinishedListener mSosTaskFinishedListener) {
        this.mContext = mContext;
        this.mSosTaskFinishedListener = mSosTaskFinishedListener;
    }

    @Override
    protected SOSResource doInBackground(String... params) {
        String webserviceData = Connection.callWebservice(params);

        return buildResult(webserviceData);
    }

    @Override
    protected void onPostExecute(SOSResource sosResource) {
        super.onPostExecute(sosResource);
        if(mSosTaskFinishedListener == null) {
            mTaskFinishedListener.OnTaskFinished(sosResource);
        }else{
            mSosTaskFinishedListener.OnTaskFinished(sosResource);
        }
    }

    private SOSResource buildResult(String webserviceData){
        SOSResource sosResource = null;
        try{
            JSONObject obj = new JSONObject(webserviceData);
            sosResource = new SOSResource(obj.optString("nomeRecurso"),obj.optString("numeroSOS"));

        }catch (JSONException jse){
            Log.e(LOG_TAG,jse.getMessage(),jse);
        }
        return sosResource;
    }
}
