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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygon;

import org.json.JSONObject;

import java.util.List;

public class GoogleMapsActivity extends MapBaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getIntentExtras(getIntent());
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Context context = getApplicationContext();
        GeoJsonLayer layer = null;

        try {
            for (int i=0; i<this.getJsonUris().size(); i++) {
                layer = new GeoJsonLayer(mMap, new JSONObject(FileUtilities.getStringFromFile(this.getJsonUris().get(i), context)));
                if (layer != null) {
                    layer.getDefaultPolygonStyle().setStrokeColor(this.getJsonColors().get(i));
                    layer.addLayerToMap();
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.geojson_opener_unable_to_read, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (layer != null) {
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getLayerBoundingBox(layer), 0));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.i("geojson-viewer", "No coordinates available to center the camera.");
            }
        }
    }

    private LatLngBounds getLayerBoundingBox(GeoJsonLayer layer){
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
