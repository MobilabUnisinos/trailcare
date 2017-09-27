package br.unisinos.hefestos.utility;

import br.unisinos.hefestos.pojo.LimitCoordinates;

public class CoordinatesUtility {

    private static final double EARTH_RADIUS = 6371d;

    public static LimitCoordinates getLimitCoordinates(double originLatitude, double originLongitude, double distanceMeters){
        double distance = distanceMeters/1000;
        double minLatitude = getMinLatitude(originLatitude,distance);
        double maxLatitude = getMaxLatitude(originLatitude, distance);

        double minLongitude = getMinLongitude(originLatitude, originLongitude, distance);
        double maxLongitude = getMaxLongitude(originLatitude, originLongitude, distance);

        if(minLatitude > maxLatitude){
            double aux = maxLatitude;
            maxLatitude = minLatitude;
            minLatitude = aux;
        }

        if(minLongitude > maxLongitude){
            double aux = maxLongitude;
            maxLongitude = minLongitude;
            minLongitude = aux;
        }

        return new LimitCoordinates(minLatitude,maxLatitude,minLongitude,maxLongitude);
    }

    private static double getMinLatitude(double originLatitude,double distance){
        return getLatitude(0, originLatitude,distance);
    }

    private static double getMaxLatitude(double originLatitude, double distance){
        return getLatitude(180, originLatitude,distance);
    }

    private static double getMinLongitude(double originLatitude, double originLongitude, double distance){
        return getLongitude(90, originLongitude, originLatitude, distance);
    }

    private static double getMaxLongitude(double originLatitude, double originLongitude, double distance){
        return getLongitude(270,originLongitude,originLatitude,distance);
    }

    private static double getLatitude(double bearing, double originLatitude,double distance) {

        double angularDistance = getAngularDistance(distance);

        originLatitude = Math.toRadians(originLatitude);
        bearing = Math.toRadians(bearing);

        double latitude = Math.asin((Math.sin(originLatitude) * Math.cos(angularDistance))
                + (Math.cos(originLatitude) * Math.sin(angularDistance) * Math.cos(bearing)));

        latitude = Math.toDegrees(latitude);

        return latitude;
    }

    private static double getLongitude(double bearing, double originLongitude,
                                       double originLatitude,  double distance) {

        double angularDistance = getAngularDistance(distance);

        originLongitude = Math.toRadians(originLongitude);
        originLatitude = Math.toRadians(originLatitude);
        double finalLatitude = Math.toRadians(getLatitude(bearing,originLatitude,distance));

        bearing = Math.toRadians(bearing);

        double longitude = originLongitude
                + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance)
                        * Math.cos(originLatitude),
                Math.cos(angularDistance)
                        - (Math.sin(originLatitude) * Math.sin(finalLatitude)));

        longitude = Math.toDegrees(longitude);

        return longitude;
    }

    private static double getAngularDistance(double distance){
        return distance / EARTH_RADIUS;
    }


    /*
    Fator de correção utilizado para calcular a ordenação por distância. Corrige distorções causadas por latitudes distantes do Equador.
     */
    public static double getCorrectionFactor(double latitude){
        return Math.pow(Math.cos(Math.toRadians(latitude)),2);
    }

    public static int distanceBetweenPoints(double lat1, double lat2, double long1, double long2) {
        Double dlon, dlat, a, distancia;

        dlon = Math.toRadians(long2 - long1);
        dlat = Math.toRadians(lat2 - lat1);
        a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        distancia = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * 6378140;
        //return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/
        return distancia.intValue();
    }
}
