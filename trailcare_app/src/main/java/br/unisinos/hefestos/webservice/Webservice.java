package br.unisinos.hefestos.webservice;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;

import br.unisinos.hefestos.pojo.PTrail;

public class Webservice {

    private static final String LOG_TAG = Webservice.class.getSimpleName();

    private static final String URL = "http://191.4.235.247:8080/WSH/recurso/";
    private static final String METHOD_OUT = "consultarRecursosOutdoor/";
    private static final String METHOD_IN = "consultarRecursosIndoor/";
    private static final String METHOD_KEYWORD = "consultarRecursosKeyword/";
    private static final String METHOD_AMB = "consultarRecursoAmb/";
    private static final String METHOD_SOS = "consultarNumeroSOS/";
    private static final String METHOD_NAME_SAVE = "saveResource/";
    private static final String METHOD_SEND_TRAIL = "regTrilha/";

    // METODO CONSULTA KEYWORD
    // *********************************************
    public static String[] consultaKey(String key, double latitude, double longitude, int quant) {
        int i=0;
        String[] params = new String[5];
        params[i++] = URL + METHOD_KEYWORD;
        params[i++] = key;
        params[i++] = String.valueOf(latitude);
        params[i++] = String.valueOf(longitude);
        params[i++] = String.valueOf(quant);

        return params;
    }

    // *********************************************

    // METODO CONSULTA TELEFONESOS
    // *********************************************
    public static String[] consultaSOS(String id_pessoa) {
        int i=0;
        String[] params = new String[2];
        params[i++] = URL + METHOD_SOS;
        params[i++] = id_pessoa;
        return params;
    }

    // *********************************************

    // METODO CONSULTA OUTDOOR
    // *********************************************
    public static String[] consultaOut(double latitude, double longitude, int quant) {
        int i=0;
        String[] params = new String[4];
        params[i++] = URL + METHOD_OUT;
        params[i++] = String.valueOf(latitude);
        params[i++] = String.valueOf(longitude);
        params[i++] = String.valueOf(quant);

        return params;
    }

    // *********************************************

    // *********************************************

    // METODO PARA CADASTRO DE NOVOS RECURSOS
    // *********************************************
    public static String[] setNewResource(String nameResource, String descriptionResource, int typeResource, double latitude, double longitude) {
        int i=0;
        String[] params = new String[6];
        params[i++] = URL + METHOD_NAME_SAVE;
        params[i++] = nameResource;
        params[i++] = descriptionResource;
        params[i++] = String.valueOf(typeResource);
        params[i++] = String.valueOf(latitude);
        params[i++] = String.valueOf(longitude);

        return params;
    }

    // *********************************************


    public static String[] consultaAmb(String codigoTag)  {
        int i=0;
        String[] params = new String[2];
        params[i++] = URL + METHOD_AMB;
        params[i++] = codigoTag;
        return params;
    }

    public static String[] sendPtrails(List<PTrail> ptrails){
        String paramSeparator = ",";
        String trailSeparator = "-";
        StringBuilder paramsBuilder = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        for (PTrail pTrail:ptrails) {

            String code = pTrail.getResource().getExternalId();
            if(pTrail.getResource().getType().equals(0)){
            }

            paramsBuilder.append("1")
                    .append(paramSeparator)
                    .append(pTrail.getResource().getType())
                    .append(paramSeparator)
                    .append(pTrail.getResource().getExternalId())
                    .append(paramSeparator)
                    .append(dateFormat.format(pTrail.getDate()))
                    .append(paramSeparator)
                    .append(hourFormat.format(pTrail.getDate()))
                    .append(trailSeparator);
        }

        Log.d(LOG_TAG,"params enviados = " + paramsBuilder.toString());

        int i=0;
        String[] params = new String[2];
        params[i++] = URL + METHOD_SEND_TRAIL;
        params[i++] = paramsBuilder.toString();

        return params;
    }

}
