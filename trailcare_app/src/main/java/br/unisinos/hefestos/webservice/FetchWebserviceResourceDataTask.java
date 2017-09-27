package br.unisinos.hefestos.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.unisinos.hefestos.RecursosKeywordActivity;
import br.unisinos.hefestos.RecursosListaActivity;
import br.unisinos.hefestos.pojo.Resource;

public class FetchWebserviceResourceDataTask extends AsyncTask<String,Void,List<Resource>> {

    private static final String LOG_TAG = FetchWebserviceResourceDataTask.class.getSimpleName();

    private Context mContext;
    private RecursosKeywordActivity.ResourceTaskFinishedListener mTaskFinishedListener;
    private RecursosListaActivity.ResourceTaskFinishedListener mRecursosListaTaskFinishedListener;

    public FetchWebserviceResourceDataTask(Context context, RecursosKeywordActivity.ResourceTaskFinishedListener taskFinishedListener) {
        mContext = context;
        this.mTaskFinishedListener= taskFinishedListener;
    }

    public FetchWebserviceResourceDataTask(Context mContext, RecursosListaActivity.ResourceTaskFinishedListener mRecursosListaTaskFinishedListener) {
        this.mContext = mContext;
        this.mRecursosListaTaskFinishedListener = mRecursosListaTaskFinishedListener;
    }

    @Override
    protected List<Resource> doInBackground(String... params) {
        String webserviceData = Connection.callWebservice(params);

        Log.v(LOG_TAG, "webserviceData = " + webserviceData);

        return buildResult(webserviceData);
    }

    @Override
    protected void onPostExecute(List<Resource> resources) {
        super.onPostExecute(resources);
        if(mTaskFinishedListener != null) {
            mTaskFinishedListener.OnTaskFinished(resources);
        }else{
            mRecursosListaTaskFinishedListener.OnTaskFinished(resources);
        }
    }

    private List<Resource> buildResult(String webserviceData){
        List<Resource> resources = new ArrayList<>();

        if(webserviceData!= null) {
            try {
                JSONObject jObj = new JSONObject(webserviceData);
                JSONArray resourcesArray = jObj.optJSONArray("recurso");
                for (int i = 0; i < resourcesArray.length(); i++) {
                    JSONObject obj = (JSONObject) resourcesArray.get(i);
                    resources.add(new Resource(obj.getInt("tipoRecurso"), obj.getString("nomeRecurso"), obj.getString("descricaoRecurso"), obj.getInt("distancia")));
                }
            } catch (JSONException jse) {
                Log.e(LOG_TAG, jse.getMessage(), jse);
            }
        }
        return resources;
    }
}
