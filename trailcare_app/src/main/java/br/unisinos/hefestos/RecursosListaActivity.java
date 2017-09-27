package br.unisinos.hefestos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unisinos.hefestos.adapters.ResourceAdapter;
import br.unisinos.hefestos.model.ResourceModel;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.pojo.Resource;
import br.unisinos.hefestos.pojo.SOSResource;
import br.unisinos.hefestos.service.BluetoothService;
import br.unisinos.hefestos.service.LocationService;
import br.unisinos.hefestos.utility.SMSUtil;
import br.unisinos.hefestos.webservice.FetchWebserviceResourceDataTask;
import br.unisinos.hefestos.webservice.FetchWebserviceSOSDataTask;
import br.unisinos.hefestos.webservice.ResourceTaskFinished;
import br.unisinos.hefestos.webservice.SOSResourceTaskFinished;
import br.unisinos.hefestos.webservice.Webservice;

public class RecursosListaActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = RecursosListaActivity.class.getSimpleName();

    public static final String ENVIRONMENT_TYPE="environment_type";
    public static final int ENVIRONMENT_OUTSIDE=0;
    public static final int ENVIRONMENT_INSIDE=1;

    public static final String ID_TAG_KEY = "idTag";

    private static int NUM_OF_DISPLAYED_RESOURCES = 5;

    private Context mContext;

    private static Double mLastLongitude;
    private static Double mLastLatitude;
    private boolean mLatitudeChanged = false;
    private boolean mLongitudeChanged = false;
    private SOSResource mSosResource;

    private List<Resource> mResources;
    private ListView mResourceslistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recursos_lista);

        // Verifica se as permissões necessárias para o GPS estão ativas
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }else{
            startLocationService();
        }

        Intent intentBluetooth = new Intent(this, BluetoothService.class);
        startService(intentBluetooth);

        //Registra o listener de alteracoes no sharedpreferences do Hefestos
        getApplication().getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);

        mResourceslistView = (ListView) findViewById(R.id.listViewOutsideResources);

        mContext = this;

        Intent intent = getIntent();
        if(intent == null || intent.getIntExtra(ENVIRONMENT_TYPE,0) == ENVIRONMENT_OUTSIDE){
            //busca outside
            if(mLastLatitude!= null && mLastLongitude != null){
                FetchWebserviceResourceDataTask fetchWebserviceResourceDataTask = new FetchWebserviceResourceDataTask(this, new ResourceTaskFinishedListener());
                fetchWebserviceResourceDataTask.execute(Webservice.consultaOut(mLastLatitude, mLastLongitude, NUM_OF_DISPLAYED_RESOURCES));
            }
            getApplication().getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        }else{
            //busca inside
            Resource resource = ResourceModel.findByTagCode(intent.getStringExtra(ID_TAG_KEY), mContext);
            Log.v(LOG_TAG, resource.toString());

            mResources = new ArrayList<>();
            mResources.add(resource);
            createListView();

            getApplication().getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    public void onConsultarButtonClick(View view) {

        if(mLastLatitude == null || mLastLongitude == null){
            Toast.makeText(mContext,R.string.location_not_found,Toast.LENGTH_LONG).show();
        }else {

            final EditText input = new EditText(mContext);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(input);
            builder.setTitle(R.string.resource_name);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    String keyword = input.getEditableText().toString();

                    Intent intent = new Intent(getApplicationContext(), RecursosKeywordActivity.class);
                    intent.putExtra(RecursosKeywordActivity.PARAMETER_TAG, keyword);
                    intent.putExtra(RecursosKeywordActivity.PARAMETER_LATITUDE, mLastLatitude);
                    intent.putExtra(RecursosKeywordActivity.PARAMETER_LONGITUDE, mLastLongitude);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onSosButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation_sms);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                FetchWebserviceSOSDataTask fetchWebserviceSOSDataTask= new FetchWebserviceSOSDataTask(mContext, new SOSResourceTaskFinishedListener());
                fetchWebserviceSOSDataTask.execute(Webservice.consultaSOS("1"));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(mContext, getString(R.string.message_not_sent), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendSMS(){
        String address = SMSUtil.buildSosSmsText(mContext,mLastLatitude,mLastLongitude);

        String sms = getString(R.string.message_sos,mSosResource.getName(),address);

        if(sms.length()>70){
            Toast.makeText(mContext, getString(R.string.message_too_long, String.valueOf(sms.length())), Toast.LENGTH_SHORT).show();
        }else{
            SMSUtil.sendSms(mSosResource,sms);
            Toast.makeText(mContext, getString(R.string.message_sent_current_locale,address), Toast.LENGTH_LONG).show();
        }
    }

    public void onMapButtonClicked(View view) {
        Intent intentIniciarMapa = new Intent(getApplicationContext(),MapActivity.class);
        intentIniciarMapa.putExtra(MapActivity.PARAMETER_MAP_TO_LOAD, MapActivity.MAP_TYPE_OUTDOOR);
        intentIniciarMapa.putExtra(MapActivity.PARAMETER_LATITUDE, mLastLatitude);
        intentIniciarMapa.putExtra(MapActivity.PARAMETER_LONGITUDE, mLastLongitude);
        intentIniciarMapa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentIniciarMapa);
    }

    public void onCadastroButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(),CadastroActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(HefestosContract.LAST_LATITUDE.equals(key)){
            mLastLatitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LATITUDE, Double.doubleToLongBits(0)));
            mLatitudeChanged=true;
        }else if(HefestosContract.LAST_LONGITUDE.equals(key)){
            mLastLongitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LONGITUDE, Double.doubleToLongBits(0)));
            mLongitudeChanged=true;
        }

        if(mLatitudeChanged && mLongitudeChanged) {
            FetchWebserviceResourceDataTask fetchWebserviceResourceDataTask = new FetchWebserviceResourceDataTask(this, new ResourceTaskFinishedListener());
            fetchWebserviceResourceDataTask.execute(Webservice.consultaOut(mLastLatitude, mLastLongitude, NUM_OF_DISPLAYED_RESOURCES));
            mLatitudeChanged=false;
            mLongitudeChanged=false;
        }
    }

    public class SOSResourceTaskFinishedListener implements SOSResourceTaskFinished {
        @Override
        public void OnTaskFinished(SOSResource sosResource) {
            mSosResource= sosResource;
            sendSMS();
        }
    }

    public class ResourceTaskFinishedListener implements ResourceTaskFinished {
        @Override
        public void OnTaskFinished(List<Resource> resources) {
            mResources = resources;
            createListView();
        }
    }

    private void createListView(){

        Log.v(LOG_TAG, "tamanho da lista retornada = " + mResources.size());

        ResourceAdapter resourceAdapter = new ResourceAdapter(mResources,this);
        mResourceslistView.setAdapter(resourceAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startLocationService(){
        // Inicia o servico de localizacao
        Intent intentLocation = new Intent(this, LocationService.class);
        startService(intentLocation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            // Inicia o servico de localizacao
            startLocationService();
        }
    }
}
