package com.nextome.geojsonify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;

class JsonifyMapBox {
    static void GeoJsonifyMap(MapboxMap mapboxMap, List<Uri> jsonUris, List<Integer> jsonColors, Context context) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        try {
            for (int i=0; i<jsonUris.size(); i++) {
                Uri uri = jsonUris.get(i);
                GeoJsonSource source = new GeoJsonSource("geojson"+i, FileUtils.getStringFromFile(uri, context));
                mapboxMap.addSource(source);
                LineLayer lineLayer = new LineLayer("geojson"+i, "geojson"+i);
                lineLayer.setProperties(lineColor(jsonColors.get(i)));
                mapboxMap.addLayer(lineLayer);

                FeatureCollection featureCollection = FeatureCollection.fromJson(FileUtils.getStringFromFile(uri, context));
                List<Feature> features = featureCollection.getFeatures();

                for (Feature f : features) {
                    if (f.getGeometry() instanceof Point) {
                        Position coordinates = (Position) f.getGeometry().getCoordinates();
                        mapboxMap.addMarker(new MarkerViewOptions()
                                .position(new LatLng(coordinates.getLatitude(), coordinates.getLongitude()))
                        );
                    } else if (f.getGeometry() instanceof com.mapbox.services.commons.geojson.Polygon){
                        com.mapbox.services.commons.geojson.Polygon polygon = (com.mapbox.services.commons.geojson.Polygon) f.getGeometry();

                        List<List<Position>> coordinates = polygon.getCoordinates();

                        for (List<Position> positions : coordinates) {
                            for (Position position : positions) {
                                boundsBuilder.include(new LatLng(position.getLatitude(), position.getLongitude()));
                            }
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 0));
        } catch (InvalidLatLngBoundsException e){
            e.printStackTrace();
            Log.i("geojson-viewer", "No coordinates available to center the camera.");
        }
    }
}
