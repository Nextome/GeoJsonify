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


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nextome.geojsonify.FileUtils;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlFolder;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class OpenStreetMapActivity extends MapBaseActivity implements LocationListener, View.OnClickListener {
    MyLocationNewOverlay myLocationOverlay = null;
    List<GeoPoint> previousLocations = new ArrayList<>();
    List<GeoPoint> routePoints = new ArrayList<>();
    List<GeoPoint> routeAhead;

    MapView map = null;
    Polyline trackLine = new Polyline();
    Polyline routeLine = new Polyline();
    boolean isCentered = false;
    int routeIndex;
    int nIndexAhead = 30;
    double initPenaltyPerIndex = 0.1;
    double updatePenaltyPerIndex = 1;

    protected LocationManager mLocationManager;
    public static KmlDocument mKmlDocument = new KmlDocument(); //made static to pass between activities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_street_map);
        this.getIntentExtras(getIntent());

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(null);

//      add user location on the map
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        map.getController().setCenter(myLocationOverlay.getMyLocation());
        map.getController().setZoom(15);

        map.getOverlays().add(myLocationOverlay);

        try {

            mKmlDocument.parseGeoJSON(FileUtils.getStringFromFile(this.getJsonUris().get(0), this.getContext()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        routeLine.setWidth(8f);
        routeLine.setColor(this.getJsonColors().get(0));
        map.getOverlayManager().add(routeLine);

        for (KmlFeature p : mKmlDocument.mKmlRoot.mItems) {
            if (p instanceof KmlPlacemark) {
                KmlPlacemark placemark = (KmlPlacemark) p;
                routePoints.add(placemark.mGeometry.mCoordinates.get(0));
            }
        }
        routeAhead = new ArrayList<>(routePoints);

        initCenterButton();
        initStartButton();
        initContinueButton();
        initProgressBar();
    }

    private void initCenterButton() {
        Button centerPositionButton = findViewById(R.id.centerButton);
        centerPositionButton.setOnClickListener(this);
    }

    private void initStartButton() {
        Button startNavigationButton = findViewById(R.id.startButton);
        startNavigationButton.setOnClickListener(this);
    }

    private void initContinueButton() {
        Button continueNavigationButton = findViewById(R.id.continueButton);
        continueNavigationButton.setOnClickListener(this);
    }

    private void initProgressBar() {
        ProgressBar progressBar = findViewById(R.id.navigationProgressBar);
        progressBar.setOnClickListener(this);
        progressBar.setMax(routePoints.size());
        progressBar.setProgress(0);
    }

    private void startLocationUpdates() {
        for (final String provider : mLocationManager.getProviders(true)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(provider, 1000, 0.0f, this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
        if (!isCentered) {
            myLocationOverlay.enableFollowLocation();
        }
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        startLocationUpdates();
    }

    private void initializeDisplayedRoute(GeoPoint currentPoint) {
        double minDistance = 10e6;
        int minDistanceIndex = 0;

        for (int i = 0; i < routePoints.size(); i = i + 1) {
            GeoPoint pt = routePoints.get(i);
            double distance = pt.distanceToAsDouble(currentPoint);
            if (distance + i * initPenaltyPerIndex <= minDistance) {
                minDistance = distance;
                minDistanceIndex = i;
            }
        }
        routeIndex = minDistanceIndex;
        routeAhead = new ArrayList<>(routePoints.subList(routeIndex, routePoints.size()));
        routeLine.setPoints(routeAhead.subList(0, nIndexAhead));
    }

    private void updateDisplayedRoute(GeoPoint currentPoint, boolean onlyDisplayed) {
        double minDistance = 10e6;
        int minDistanceIndex = 0;

        int nSearch = nIndexAhead;
        if (!onlyDisplayed){
            nSearch = routeAhead.size();
        }
        for (int i = 0; i < routeAhead.subList(0, nSearch).size(); i = i + 1) {
            GeoPoint pt = routeAhead.subList(0, nSearch).get(i);
            double distance = pt.distanceToAsDouble(currentPoint);
            if (distance + i * updatePenaltyPerIndex <= minDistance) {
                minDistance = distance;
                minDistanceIndex = i;
            }
        }
        routeIndex += minDistanceIndex;
        routeAhead = new ArrayList<>(routeAhead.subList(minDistanceIndex, routeAhead.size()));
        routeLine.setPoints(routeAhead.subList(0, nIndexAhead));
    }

    private void reCenterOnRoute(GeoPoint locationPoint){
        List<GeoPoint> positionAndTraj = new ArrayList<>(routePoints.subList(routeIndex, routeIndex + nIndexAhead));
        positionAndTraj.add(locationPoint);
        Polyline polylineToCenter = new Polyline();
        polylineToCenter.setVisible(false);
        polylineToCenter.setPoints(positionAndTraj);
        BoundingBox boundingBox =  polylineToCenter.getBounds();
        map.zoomToBoundingBox(boundingBox.increaseByScale(1.3f), true);

    }

    @Override
    public void onLocationChanged(final Location pLoc) {
        GeoPoint newLocation = new GeoPoint(pLoc);
        updateDisplayedRoute(newLocation, true);
        ProgressBar progressBar = findViewById(R.id.navigationProgressBar);
        progressBar.setProgress(routeIndex);

        if (isCentered) {
            reCenterOnRoute(newLocation);
        }

        previousLocations.add(newLocation);
        trackLine.setPoints(previousLocations);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.centerButton:
                Button centerPositionButton = findViewById(R.id.centerButton);
                if (isCentered){
                    isCentered = false;
                    centerPositionButton.setText(R.string.osm_center);
                }
                else {
                    isCentered = true;
                    myLocationOverlay.disableFollowLocation();
                    reCenterOnRoute(myLocationOverlay.getMyLocation());
                    centerPositionButton.setText(R.string.osm_unlock);
                }
                break;
            case R.id.startButton:
                initializeDisplayedRoute(new GeoPoint(myLocationOverlay.getMyLocation()));
                break;
            case R.id.continueButton:
                updateDisplayedRoute(new GeoPoint(myLocationOverlay.getMyLocation()), false);
                break;
            default:
                break;
        }
    }
    }