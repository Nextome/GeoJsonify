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
