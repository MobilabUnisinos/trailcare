package br.unisinos.hefestos.webservice;

import android.os.AsyncTask;

public class SendPTrailWebserviceTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        return Connection.callWebservice(params);
    }
}
