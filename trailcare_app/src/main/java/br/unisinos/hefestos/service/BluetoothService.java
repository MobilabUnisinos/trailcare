package br.unisinos.hefestos.service;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import br.unisinos.hefestos.BuildConfig;
import br.unisinos.hefestos.RecursosListaActivity;
import br.unisinos.hefestos.model.PTrailModel;
import br.unisinos.hefestos.pojo.PTrail;
import br.unisinos.hefestos.webservice.Connection;
import br.unisinos.hefestos.webservice.Webservice;

public class BluetoothService extends IntentService{

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String LOG_TAG = BluetoothService.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket mSocket;

    public BluetoothService(String name) {
        super(name);
    }

    public BluetoothService() {
        super(BluetoothService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(LOG_TAG,"bluetooth nao suportado");
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.stopSelf();
            return;
        }

        Log.d(LOG_TAG,"vai testar se bluetooth esta ligado");

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(LOG_TAG,"bluetooth vai ser ligado");
            mBluetoothAdapter.enable();
        }

        mBluetoothAdapter.cancelDiscovery();

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:11:11:13:40:83");
        mSocket = null;

        InputStream inputStream = null;
        try {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();

            inputStream = mSocket.getInputStream();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(),e);
        }

        if (BuildConfig.DEBUG){
//            inputStream = new InputStream() {
//                @Override
//                public int read() throws IOException {
//                    return 0;
//                }
//            };
        }

        if(inputStream!= null ) {
            readTags(inputStream);
        }
    }

    private void readTags(InputStream inputStream){
        int bytes;
        String data;
        String tag_pai = "";

        boolean envi_indoor = false;

        Message message = null;
        while (true) {
            try {
                data = "";
                while (data.length() != 8) {
                    bytes = inputStream.read();
                    data += (char) bytes;
                    data = data.trim();

//                    if(BuildConfig.DEBUG) {
//                        data = "14750364";
//                    }

                    if (data.length() == 8) {

                        message = handler.obtainMessage();
                        message.arg1=1;
                        message.arg2=Integer.parseInt(data);
                        handler.sendMessage(message);

                        Log.i(LOG_TAG, "tag = " + data);

                        String result = Connection.callWebservice(Webservice.consultaAmb(data));

                        Log.v(LOG_TAG, "resultado da busca result  = " + result);

                        //result = "{\"recurso\":{\"idPai\":\"1\"}}";
                        if (result != null){
                            JSONObject jObj = new JSONObject(result);
                            JSONObject recurso = jObj.getJSONObject("recurso");
                            String retorno_WS = recurso.getString("idPai");

//                        if(BuildConfig.DEBUG){
//                            retorno_WS = "14750364";
//                        }

                            //String retorno_WS = "1";
                            Intent intent = new Intent(getApplicationContext(), RecursosListaActivity.class);
                            Log.v(LOG_TAG, "envi_indoor = " +envi_indoor);
                            if(envi_indoor){
                                if(tag_pai.equals(retorno_WS)){
                                    envi_indoor = false;
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(RecursosListaActivity.ENVIRONMENT_TYPE,RecursosListaActivity.ENVIRONMENT_OUTSIDE);
                                }else{
                                    intent.putExtra(RecursosListaActivity.ID_TAG_KEY, data);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(RecursosListaActivity.ENVIRONMENT_TYPE, RecursosListaActivity.ENVIRONMENT_INSIDE);

                                    creatPtrail(data);
                                }
                            }else{
                                envi_indoor = true;
                                //tag_pai = retorno_WS;
                                tag_pai = "1";
                                intent.putExtra(RecursosListaActivity.ID_TAG_KEY, data);
                                intent.putExtra(RecursosListaActivity.ENVIRONMENT_TYPE, RecursosListaActivity.ENVIRONMENT_INSIDE);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                creatPtrail(data);
                            }

                            startActivity(intent);
                        }

                    }
                }
                Thread.sleep(100);
            } catch (IOException e) {
                break;
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    //MOSTRA TAG LIDA COM TOAST
    //***************************************
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                String aux = Integer.toString(msg.arg2);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(),e);
        }
    }

    private void creatPtrail(String tag){
        PTrail pTrail = PTrailModel.insert(tag,this);

        if (pTrail != null){
            Log.d(LOG_TAG,"inseriu ptrail, id = " + pTrail.getId());
        }
    }
}
