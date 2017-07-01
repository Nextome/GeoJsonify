/*
 * Copyright 2017 Nextome S.r.l
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nextome.geojsonviewer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Geometry;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.io.IOException;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;

public class MapBoxActivity extends MapBaseActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getIntentExtras(getIntent());
        Mapbox.getInstance(this.getContext(), getString(R.string.mapbox_key));

        setContentView(R.layout.activity_map_box);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        try {
            for (int i=0; i<this.getJsonUris().size(); i++) {
                Uri uri = this.getJsonUris().get(i);
                GeoJsonSource source = new GeoJsonSource("geojson"+i, FileUtilities.getStringFromFile(uri, this.getContext()));
                mapboxMap.addSource(source);
                LineLayer lineLayer = new LineLayer("geojson"+i, "geojson"+i);
                lineLayer.setProperties(lineColor(this.getJsonColors().get(i)));
                mapboxMap.addLayer(lineLayer);

                FeatureCollection featureCollection = FeatureCollection.fromJson(FileUtilities.getStringFromFile(uri, this.getContext()));
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


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
