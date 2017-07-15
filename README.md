# GeoJsonify
## Simply add GeoJson layers to your Maps
[![](https://jitpack.io/v/Nextome/GeoJsonify.svg)](https://jitpack.io/#Nextome/GeoJsonify)
<br>
### Supported Map Services:
 * [Google Maps](https://maps.google.com/);
 * [Open Street Map](https://www.openstreetmap.org);
 * [MapBox](https://www.mapbox.com/);

### How to use
1. Add JitPack in your **root build.gradle** at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency in your **module build.gradle**:
```gradle
	dependencies {
	        compile 'com.github.Nextome:GeoJsonify:v1.0.0'
	}

```

3. Simply use the method:
```java
GeoJsonify.geoJsonifyMap(map, List<Uri> jsonUris, int color, Context context);
```

* **map** is the map where the layers will be added.
It can be a *GoogleMap* (Google Maps) / *MapboxMap* (Mapbox) / *MapView* (OSM)

* **jsonUris** is a list of URIs, each one with a different .geojson file to parse.
* **color** is the color of the lines that will be rendered.

Alternativly, you can also specify a different color for each layer using
```java
GeoJsonify.geoJsonifyMap(map, List<Uri> jsonUris, List<Integer> colors, Context context);
```

### Javadoc
Available [here](https://nextome.github.io/GeoJsonify/index.html).

### Example (Google Maps)
Here's a full Google Maps implementation:
```java
public class GoogleMapsActivity extends MapBaseActivity implements OnMapReadyCallback {

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
        Uri uri1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "italy.geojson"));
        Uri uri2 = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "puglia.geojson"));

        List<Uri> URIs = new ArrayList<>();
        URIs.add(uri1);
        URIs.add(uri2);
        
        try {
            GeoJsonify.geoJsonifyMap(googleMap, URIs, Color.BLACK, this.getContext());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Unable to read file", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Unable to parse file", Toast.LENGTH_SHORT).show();
        }
    }
}
```

![](https://lh3.googleusercontent.com/IOziKkwBfPIyOLsQhWddI36wqQJs2lHB34g8A2JyrYrnTNp6Q3HCrtkIkfAdB8qWppgA=h900-rw)


For more examples with all services, see our example app **GeoJson Viewer**
<br>
<br>
<br>
# GeoJson Viewer
## View GeoJson files on your Android Device

[![](https://github.com/Nextome/geojson-viewer/blob/master/resources/cover.jpg)](https://youtu.be/qo7hc_iLI6s)

<a href='com.nextome.geojsonviewer?pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width="320" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>


## How to use
Simply choose a _GeoJson_ file on your device and select a map provider from the list.

Supported Map Services:
 * [Google Maps](https://maps.google.com/);
 * [Open Street Map](https://www.openstreetmap.org);
 * [MapBox](https://www.mapbox.com/);
 
## Examples
#### Map with Polygons, Lines and Points
<img src="https://github.com/Nextome/geojson-viewer/blob/master/resources/example_path.png" width="480">

#### Different Services
Same _.geojson_ file opened with Google Maps, Open Street Map and MapBox

<img src="https://github.com/Nextome/geojson-viewer/blob/master/resources/example_gmaps.png" width="200"> <img src="https://github.com/Nextome/geojson-viewer/blob/master/resources/example_osm.png" width="200"> <img src="https://github.com/Nextome/geojson-viewer/blob/master/resources/example_mapbox.png" width="200">

## How to build the project
 * Clone the project;
 * Open Android Studio and select Import Project;
 * Add your own Google Maps and Mapbox API keys in _strings.xml_;
 
We'll also be happy to accept your pull requests.
 
## Read More
Read more about adding a GeoJson layer on maps on our [blog](https://medium.com/nextome/show-a-geojson-layer-on-google-maps-osm-mapbox-on-android-cd75b8377ba).
 

## Install the app
GeoJson Viewer is available for free on [Google Play](https://play.google.com/store/apps/details?id=com.nextome.geojsonviewer).

## Licence
GeoJson Viewer is licensed under the [Apache License 2.0](https://github.com/Nextome/geojson-viewer/blob/master/LICENSE).


Made at [Nextome](http://nextome.org/).
