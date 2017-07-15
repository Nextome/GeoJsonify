/*
 * Copyright 2017 Nextome S.r.l
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nextome.geojsonviewer;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nextome.geojsonify.GeoJsonify;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.io.IOException;


public class OpenStreetMapActivity extends MapBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_street_map);
        this.getIntentExtras(getIntent());

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(4);
        map.setMaxZoomLevel(null);

        try {
            GeoJsonify.geoJsonifyMap(map, this.getJsonUris(), this.getJsonColors(), this.getContext());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), R.string.geojson_opener_unable_to_read, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

}
