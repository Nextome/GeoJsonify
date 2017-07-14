package com.nextome.geojsonify;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonify {

    /**
     * Add GeoJson features to a Google Maps map.
     * Accepts a list of uris, each one with a different .geojson file to parse
     * Each layer will be added on top of the other based on the order in the list
     *
     *
     * @param map GoogleMap object where to add GeoJson layers
     * @param jsonUris list of uris of .geojson files
     * @param jsonColors list of colors for each geojson layer
     * @param context application context
     * @throws IOException if unable to open file
     * @throws JSONException if unable to parse JSON content
     */
    public static void geoJsonifyMap(GoogleMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException, JSONException {
        JsonifyGoogleMaps.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    /**
     * Works just like {@link GeoJsonify#geoJsonifyMap(GoogleMap, List, List, Context)} except
     * all the added GeoJson layers will be the same color
     *
     * @param color color for all geojson layers
     * @see GeoJsonify#geoJsonifyMap(GoogleMap, List, List, Context)
     */
    public static void geoJsonifyMap(GoogleMap map, List<Uri> jsonUris, int color, Context context) throws IOException, JSONException {
        JsonifyGoogleMaps.geoJsonifyMap(map, jsonUris, generateColorsList(color, jsonUris.size()), context);
    }


    /**
     * Add GeoJson features to a Mapbox map.
     * Accepts a list of uris, each one with a different .geojson file to parse
     * Each layer will be added on top of the other based on the order in the list
     *
     *
     * @param map MapboxMap object where to add GeoJson layers
     * @param jsonUris list of uris of .geojson files
     * @param jsonColors list of colors for each geojson layer
     * @param context application context
     * @throws IOException if unable to open file
     */
    public static void geoJsonifyMap(MapboxMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException {
        JsonifyMapBox.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    /**
     * Works just like {@link GeoJsonify#geoJsonifyMap(MapboxMap, List, List, Context)} except
     * all the added GeoJson layers will be the same color
     *
     * @param color color for all geojson layers
     * @see GeoJsonify#geoJsonifyMap(MapboxMap, List, List, Context)
     */
    public static void geoJsonifyMap(MapboxMap map, List<Uri> jsonUris, int color, Context context) throws IOException {
        JsonifyMapBox.geoJsonifyMap(map, jsonUris, generateColorsList(color, jsonUris.size()), context);
    }

    /**
     * Add GeoJson features to a Open Street Maps map.
     * Accepts a list of uris, each one with a different .geojson file to parse
     * Each layer will be added on top of the other based on the order in the list
     *
     *
     * @param map MapView object where to add GeoJson layers
     * @param jsonUris list of uris of .geojson files
     * @param jsonColors list of colors for each geojson layer
     * @param context application context
     * @throws IOException if unable to open file
     */
    public static void geoJsonifyMap(MapView map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException {
        JsonifyOSM.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    /**
     * Works just like {@link GeoJsonify#geoJsonifyMap(MapView, List, List, Context)} except
     * all the added GeoJson layers will be the same color
     *
     * @param color color for all geojson layers
     * @see GeoJsonify#geoJsonifyMap(MapView, List, List, Context)
     */
    public static void geoJsonifyMap(MapView map, List<Uri> jsonUris, int color, Context context) throws IOException {
        JsonifyOSM.geoJsonifyMap(map, jsonUris, generateColorsList(color, jsonUris.size()), context);
    }

    private static List<Integer> generateColorsList(int color, int size) {
        List<Integer> jsonColors = new ArrayList<>();
        for (int i=0; i<size; i++){
            jsonColors.add(color);
        }

        return jsonColors;
    }
}
