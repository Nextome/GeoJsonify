package com.nextome.geojsonviewer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

public abstract class MapBaseActivity extends AppCompatActivity {
    private ArrayList<String> jsonUriStrings;
    private ArrayList<Uri> jsonUris = new ArrayList<>();
    private ArrayList<Integer> jsonColors = new ArrayList<>();
    private Context context;

    protected void getIntentExtras(Intent intent){
        context = getApplicationContext();
        jsonUriStrings = intent.getStringArrayListExtra(GeoJsonViewerConstants.INTENT_EXTRA_JSON_URI);
        jsonColors = intent.getIntegerArrayListExtra(GeoJsonViewerConstants.INTENT_EXTRA_JSON_COLORS);

        for (String uri:jsonUriStrings){
            jsonUris.add(Uri.parse(uri));
        }
    }

    public ArrayList<String> getJsonUriStrings() {
        return jsonUriStrings;
    }

    public ArrayList<Uri> getJsonUris() {
        return jsonUris;
    }

    public ArrayList<Integer> getJsonColors() {
        return jsonColors;
    }

    public Context getContext() {
        return context;
    }
}
