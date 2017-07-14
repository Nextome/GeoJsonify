package com.nextome.geojsonify;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

class JsonifyGoogleMaps {
    static void geoJsonifyMap(GoogleMap map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException, JSONException {
        GeoJsonLayer layer = null;

        for (int i=0; i<jsonUris.size(); i++) {
            layer = new GeoJsonLayer(map, new JSONObject(FileUtils.getStringFromFile(jsonUris.get(i), context)));
            if (layer != null) {
                layer.getDefaultPolygonStyle().setStrokeColor(jsonColors.get(i));
                layer.addLayerToMap();
            }
        }

        if (layer != null) {
            try {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(getLayerBoundingBox(layer), 0));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.i("geojson-viewer", "No coordinates available to center the camera.");
            }
        }
    }

    private static LatLngBounds getLayerBoundingBox(GeoJsonLayer layer){
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature.hasGeometry()) {
                GeoJsonGeometry geometry = feature.getGeometry();

                if (geometry instanceof GeoJsonPolygon) {
                    List<? extends List<LatLng>> lists =
                            ((GeoJsonPolygon) geometry).getCoordinates();

                    for (List<LatLng> list : lists) {
                        for (LatLng latLng : list) {
                            builder.include(latLng);
                        }
                    }
                }
            }
        }

        return builder.build();
    }
}
