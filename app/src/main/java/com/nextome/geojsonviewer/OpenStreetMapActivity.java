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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;


public class OpenStreetMapActivity extends AppCompatActivity {

    Uri fileUri;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_street_map);
        context = getApplicationContext();

        fileUri = Uri.parse(getIntent().getStringExtra(GeoJsonViewerConstants.INTENT_EXTRA_JSON_URI));


        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(4);
        map.setMaxZoomLevel(null);

        try {
            KmlDocument kmlDocument = new KmlDocument();
            Log.e("ntm", FileUtilities.getStringFromFile(fileUri, context));
            kmlDocument.parseGeoJSON(FileUtilities.getStringFromFile(fileUri, context));

            Drawable defaultMarker = getResources().getDrawable(R.drawable.marker_default);
            Bitmap defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
            Style defaultStyle = new Style(defaultBitmap, 0x901010AA, 5f, 0x20AA1010);
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(map, defaultStyle, null, kmlDocument);

            map.getOverlays().add(geoJsonOverlay);
            map.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.geojson_opener_unable_to_read, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

}
