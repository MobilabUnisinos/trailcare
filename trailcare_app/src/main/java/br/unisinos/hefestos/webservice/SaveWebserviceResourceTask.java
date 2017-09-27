package br.unisinos.hefestos.webservice;

import android.os.AsyncTask;
import android.util.Log;

import br.unisinos.hefestos.CadastroActivity;

public class SaveWebserviceResourceTask extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = SaveWebserviceResourceTask.class.getSimpleName();

    private CadastroActivity.SaveResourceTaskFinishedListener mSaveResourceTaskFinishedListener;

    public SaveWebserviceResourceTask(CadastroActivity.SaveResourceTaskFinishedListener saveResourceTaskFinishedListener) {
        this.mSaveResourceTaskFinishedListener= saveResourceTaskFinishedListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String webserviceData = Connection.callWebservice(params);
        return webserviceData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mSaveResourceTaskFinishedListener.OnTaskFinished((s== null?false:true));
    }
}
