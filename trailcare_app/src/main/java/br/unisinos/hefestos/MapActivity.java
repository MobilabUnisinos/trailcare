package br.unisinos.hefestos;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import br.unisinos.hefestos.utility.CoordinatesUtility;

public class MapActivity extends AppCompatActivity {

    public static final String PARAMETER_MAP_TO_LOAD = "idMapToLoad";
    public static final String PARAMETER_LATITUDE = "lat";
    public static final String PARAMETER_LONGITUDE = "lon";

    public static final String MAP_TYPE_OUTDOOR = "outdoor1";
    public static final String MAP_TYPE_INDOOR = "indoor";

    private ImageView mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        String idMap = intent.getStringExtra("idMapToLoad");
        String idTag = intent.getStringExtra("tag");
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lon = intent.getDoubleExtra("lon", 0.0);
        int dist = CoordinatesUtility.distanceBetweenPoints(lat,-29.7944745,lon,-51.155055499999996);

        mMap = (ImageView) findViewById(R.id.mapaEstatico);

        if(MAP_TYPE_OUTDOOR.equalsIgnoreCase(idMap)){
            if(dist <= 200){
                mMap.setImageResource(R.drawable.mapa1);
            }else{
                mMap.setImageResource(R.drawable.mapa2);
            }
        } else if(MAP_TYPE_INDOOR.equalsIgnoreCase(idMap)){
            if("8".equals(idTag)){
                mMap.setImageResource(R.drawable.mapa3);
            }else{
                mMap.setImageResource(R.drawable.mapa4);
            }
        }

    }


}
