package br.unisinos.hefestos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.unisinos.hefestos.adapters.ResourceAdapter;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.pojo.Resource;
import br.unisinos.hefestos.pojo.SOSResource;
import br.unisinos.hefestos.utility.SMSUtil;
import br.unisinos.hefestos.webservice.FetchWebserviceResourceDataTask;
import br.unisinos.hefestos.webservice.FetchWebserviceSOSDataTask;
import br.unisinos.hefestos.webservice.ResourceTaskFinished;
import br.unisinos.hefestos.webservice.SOSResourceTaskFinished;
import br.unisinos.hefestos.webservice.Webservice;


public class RecursosKeywordActivity extends Activity {

    private static final String LOG_TAG = RecursosKeywordActivity.class.getSimpleName();

    public static final String PARAMETER_LATITUDE = "latitude";
    public static final String PARAMETER_LONGITUDE = "longitude";
    public static final String PARAMETER_TAG = "idTag";

    private Context mContext;
    private List<Resource> mResources;
    private ListView mResourceslistView;
    private SOSResource mSosResource;
    private static Double mLastLongitude;
    private static Double mLastLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recursos_keyword);

        mContext = this;
        mResourceslistView = (ListView)findViewById(R.id.listViewResources);

        String keyword = getIntent().getStringExtra(PARAMETER_TAG);
        double latitude = getIntent().getDoubleExtra(PARAMETER_LATITUDE,0);
        double longitude = getIntent().getDoubleExtra(PARAMETER_LONGITUDE,0);

        FetchWebserviceResourceDataTask fetchWebserviceResourceDataTask = new FetchWebserviceResourceDataTask(this, new ResourceTaskFinishedListener());
        fetchWebserviceResourceDataTask.execute(Webservice.consultaKey(keyword, latitude, longitude, 10));

        SharedPreferences sharedPreferences = getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mLastLatitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LATITUDE, Double.doubleToLongBits(0)));
        mLastLongitude = Double.longBitsToDouble(sharedPreferences.getLong(HefestosContract.LAST_LONGITUDE, Double.doubleToLongBits(0)));

    }

    private void createListView(){
        ResourceAdapter resourceAdapter = new ResourceAdapter(mResources,this);
        mResourceslistView.setAdapter(resourceAdapter);
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
        String address = SMSUtil.buildSosSmsText(mContext, mLastLatitude, mLastLongitude);

        String sms = getString(R.string.message_sos, mSosResource.getName(), address);

        if(sms.length()>70){
            Toast.makeText(mContext, getString(R.string.message_too_long,sms.length()), Toast.LENGTH_SHORT).show();
        }else{
            SMSUtil.sendSms(mSosResource,sms);
            Toast.makeText(mContext, getString(R.string.message_sent_current_locale,address), Toast.LENGTH_LONG).show();
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
}
