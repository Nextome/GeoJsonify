package com.nextome.geojsonify;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.List;

public class GeoJsonify {
    public static void geoJsonifyMap(GoogleMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException, JSONException {
        JsonifyGoogleMaps.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    public static void geoJsonifyMap(MapboxMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException {
        JsonifyMapBox.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }

    public static void geoJsonifyMap(MapView map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException {
        JsonifyOSM.geoJsonifyMap(map, jsonUris, jsonColors, context);
    }
}
