package br.unisinos.hefestos.utility;

import java.util.Date;

import br.unisinos.hefestos.R;

public class Utility {

    public static int getResourceIconId(int resourceId){
        int iconId = 0;
        switch (resourceId) {
            case 1:
                iconId = R.drawable.parking;
                break;
            case 2:
                iconId = R.drawable.ramp;
                break;
            case 3:
                iconId = R.drawable.elevator;
                break;
            case 4:
                iconId = R.drawable.toilet;
                break;
            case 5:
                iconId = R.drawable.general;
                break;
            case 6:
                iconId = R.drawable.general;
                break;
            case 7:
                iconId = R.drawable.food;
                break;
            case 8:
                iconId = R.drawable.telephone;
                break;
            case 9:
                iconId = R.drawable.bank;
                break;
            case 10:
                iconId = R.drawable.general;
                break;
            case 11:
                iconId = R.drawable.general;
                break;
            case 12:
                iconId = R.drawable.general;
                break;
            default:
                iconId = R.drawable.general;
                break;
        }
        return iconId;
    }

    public static long getTimeDifferenteInSeconds(long lastTime){
        long diff = (new Date()).getTime() - lastTime;
        return diff/1000;
    }
}
