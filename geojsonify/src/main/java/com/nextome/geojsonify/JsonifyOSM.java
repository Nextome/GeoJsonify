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

package com.nextome.geojsonify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;

import java.io.IOException;
import java.util.List;

public class JsonifyOSM {
    static void geoJsonifyMap(final MapView map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) throws IOException {
        final KmlDocument kmlDocument = new KmlDocument();

        for (int i=0; i<jsonUris.size(); i++) {
            Uri uri = jsonUris.get(i);
            kmlDocument.parseGeoJSON(FileUtils.getStringFromFile(uri, context));

            Drawable defaultMarker = context.getResources().getDrawable(R.drawable.marker_default);
            Bitmap defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
            Style defaultStyle = new Style(defaultBitmap, jsonColors.get(i), 2f, 0x00000000);
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(map, defaultStyle, null, kmlDocument);

            map.getOverlays().add(geoJsonOverlay);
            map.invalidate();
        }


        // Workaround for osmdroid issue
        // See: https://github.com/osmdroid/osmdroid/issues/337
        map.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {
            @Override
            public void onFirstLayout(View v, int left, int top, int right, int bottom) {
                BoundingBox boundingBox = kmlDocument.mKmlRoot.getBoundingBox();
                // Yep, it's called 2 times. Another workaround for zoomToBoundingBox.
                // See: https://github.com/osmdroid/osmdroid/issues/236#issuecomment-257061630
                map.zoomToBoundingBox(boundingBox, false);
                map.zoomToBoundingBox(boundingBox, false);
                map.invalidate();
            }
        });
    }
}
