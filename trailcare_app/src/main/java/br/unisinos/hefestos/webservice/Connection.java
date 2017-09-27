package br.unisinos.hefestos.webservice;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class Connection {

    private static final String LOG_TAG = Connection.class.getSimpleName();

    public static String callWebservice(String[] params){
        String jsonText = null;

        Log.v(LOG_TAG,"entrou em callws");

        try {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri.Builder b = Uri.parse(params[0]).buildUpon();

            for (int i=1; i<params.length;i++){
                String p = params[i];
                if (p != null && p.length() > 0 && !"null".equals(p) ){
                    b.appendPath(p);
                }
                
            }

            Uri builtUri = b.build();
            b.encodedQuery("");

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG,"montou url, url  = " + builtUri.toString());
            Log.v(LOG_TAG,"montou url, url  = " + url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "conectou");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() != 0) {
                    jsonText  = buffer.toString();
                }
            }

            Log.v(LOG_TAG, "RequestJson, retorno= " + jsonText);
            return jsonText;
        }catch (Exception e){
            e.printStackTrace();
            Log.d(LOG_TAG,"RequestJson: ", e);
            //Log.e(LOG_TAG,e.getMessage());
        }
        return jsonText;
    }
}
