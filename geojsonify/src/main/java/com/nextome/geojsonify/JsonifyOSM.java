package com.nextome.geojsonify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;

import java.util.List;

public class JsonifyOSM {
    static void geoJsonifyMap(final MapView map, List<Uri> jsonUris, List<Integer> jsonColors, Context context) {
        final KmlDocument kmlDocument = new KmlDocument();

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to read file", Toast.LENGTH_LONG).show();
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
