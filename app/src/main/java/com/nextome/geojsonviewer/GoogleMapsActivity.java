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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.geojson.GeoJsonLayer;

import org.json.JSONObject;

import java.util.ArrayList;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<String> jsonUriStrings;
    private ArrayList<Uri> jsonUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        jsonUriStrings = getIntent().getStringArrayListExtra(GeoJsonViewerConstants.INTENT_EXTRA_JSON_URI);
        for (String uri:jsonUriStrings){
            jsonUris.add(Uri.parse(uri));
        }

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
            for (Uri uri:jsonUris)
            layer = new GeoJsonLayer(mMap, new JSONObject(FileUtilities.getStringFromFile(uri, context)));
            if (layer!=null) {
                layer.addLayerToMap();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.geojson_opener_unable_to_read, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
