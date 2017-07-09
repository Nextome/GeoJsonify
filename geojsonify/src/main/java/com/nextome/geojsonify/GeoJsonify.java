package com.nextome.geojsonify;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.osmdroid.views.MapView;

import java.util.List;

public class GeoJsonify {
    public static void geoJsonifyMap(GoogleMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context){
        JsonifyGoogleMaps.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    public static void geoJsonifyMap(MapboxMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context){
        JsonifyMapBox.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    public static void geoJsonifyMap(MapView map, List<Uri> jsonUris, List<Integer> jsonColors, Context context){
        JsonifyOSM.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }
}
