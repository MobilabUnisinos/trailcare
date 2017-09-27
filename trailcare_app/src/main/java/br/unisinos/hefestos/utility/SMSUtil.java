package br.unisinos.hefestos.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import br.unisinos.hefestos.pojo.SOSResource;

public class SMSUtil {

    private static final String LOG_TAG = SMSUtil.class.getSimpleName();

    public static String buildSosSmsText(Context context, Double latitude, Double longitude) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context);

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getAddressLine(1);
        String country = addresses.get(0).getAddressLine(2);

        return address;
    }


    public static void sendSms(SOSResource sosResource, String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sosResource.getTelephoneNumber(), null, message, null, null);
    }


}
