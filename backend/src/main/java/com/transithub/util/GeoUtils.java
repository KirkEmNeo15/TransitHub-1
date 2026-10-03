package com.transithub.util;

/** Distance calculations between map coordinates. */
public final class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_DEGREE_OF_LATITUDE = 111.0;

    private GeoUtils() {
    }

    /** Straight-line distance over the Earth's surface in km (haversine formula). */
    public static double distanceKm(double latitude1, double longitude1,
                                    double latitude2, double longitude2) {
        double latDifference = Math.toRadians(latitude2 - latitude1);
        double lonDifference = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDifference / 2) * Math.sin(latDifference / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDifference / 2) * Math.sin(lonDifference / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /** How many degrees of latitude equal this many km. */
    public static double kmToLatitudeDegrees(double km) {
        return km / KM_PER_DEGREE_OF_LATITUDE;
    }

    /** How many degrees of longitude equal this many km at the given latitude. */
    public static double kmToLongitudeDegrees(double km, double atLatitude) {
        double scale = Math.max(Math.cos(Math.toRadians(atLatitude)), 0.01); // avoid dividing by ~0 near the poles
        return km / (KM_PER_DEGREE_OF_LATITUDE * scale);
    }
}
