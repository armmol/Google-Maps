package com.example.final_appdevelopment_maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.final_appdevelopment_maps._direction.DirectionsAPICallback;
import com.example.final_appdevelopment_maps._direction.GetURL;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.final_appdevelopment_maps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionsAPICallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private MarkerOptions loc1, loc2;
    private Polyline polyline;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_maps);

        Button direction = findViewById (R.id.getDirection);
        Button polygon = findViewById (R.id.getpolygons);
        Button liveloc = findViewById (R.id.getLive);

        ArrayList<LatLng> area1 = new ArrayList<> ();
        area1.add (new LatLng (54.899300, 23.886297));
        area1.add (new LatLng (54.899306, 23.883121));
        area1.add (new LatLng (54.899004, 23.882112));
        area1.add (new LatLng (54.898276, 23.885384));

        ArrayList<LatLng> area2 = new ArrayList<> ();
        area2.add (new LatLng (54.672362, 25.279918));
        area2.add (new LatLng (54.671165, 25.279296));
        area2.add (new LatLng (54.670966, 25.280583));
        area2.add (new LatLng (54.672108, 25.281677));

        loc1 = new MarkerOptions ().position (new LatLng (54.899288, 23.886308));
        loc2 = new MarkerOptions ().position (new LatLng (54.668965, 25.278990));

        liveloc.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (ActivityCompat.checkSelfPermission (MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission (MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions (MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
                    ActivityCompat.requestPermissions (MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
                }
                mMap.setMyLocationEnabled (true);
                mMap.getUiSettings ().setMyLocationButtonEnabled (true);
                mMap.getUiSettings ().setAllGesturesEnabled (true);
                mMap.getUiSettings ().setZoomControlsEnabled (true);

                client = LocationServices.getFusedLocationProviderClient (MapsActivity.this);
                Task<Location> task = client.getLastLocation ();
                task.addOnSuccessListener (new OnSuccessListener<Location> () {
                    @Override
                    public void onSuccess (Location location) {
                        mMap.addMarker (new MarkerOptions ().position (new LatLng (location.getLatitude (), location.getLongitude ())));
                    }
                });

            }
        });
        direction.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                new GetURL (MapsActivity.this)
                        .execute (geturl (loc1.getPosition (),loc2.getPosition (),"driving"), "driving");

                mMap.addMarker (loc1);mMap.addMarker (loc2);
                mMap.moveCamera (CameraUpdateFactory.newLatLng (loc1.getPosition ()));
                mMap.setMinZoomPreference (15f);
            }
        });

        polygon.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                getpolygons(mMap, area1, area2);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager ()
                .findFragmentById (R.id.map);
        mapFragment.getMapAsync (this);
    }

    private void getpolygons (GoogleMap mMap, ArrayList<LatLng> area1, ArrayList<LatLng> area2) {
        PolygonOptions polygonOptions = new PolygonOptions ();
        for (LatLng lt: area1) {
            polygonOptions.add(lt);
        }

        Polygon polygon = mMap.addPolygon (polygonOptions);
        stylepolygon(polygon);

        PolygonOptions polygonOptions1 = new PolygonOptions ();
        for (LatLng lt: area2) {
            polygonOptions1.add(lt);
        }

        Polygon polygon1 = mMap.addPolygon (polygonOptions1);
        stylepolygon(polygon1);

    }

    private static final PatternItem DOT = new Dot ();
    private static final PatternItem GAP = new Gap (20);
    private static final PatternItem DASH = new Dash (20);

    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList (GAP, DASH);
    private static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList (DOT, GAP, DASH, GAP);
    private void stylepolygon (Polygon polygon) {
        List<PatternItem> pattern = null;
        int strokeColor = 0xffF57F17;
        pattern = PATTERN_POLYGON_BETA;
        polygon.setStrokePattern (pattern);
        polygon.setClickable (true);
        polygon.setStrokeWidth (10);
        polygon.setStrokeColor (strokeColor);
        polygon.setFillColor (0xffF9A825);
    }

    @Override
    public void onMapReady (GoogleMap googleMap) {
        mMap = googleMap;

    }

    private String geturl(LatLng origin, LatLng destination, String directionmode){
        String ORI = "origin="+origin.latitude+","+origin.longitude;
        String DES = "destination="+destination.latitude+","+destination.longitude;
        String MODE = "mode="+directionmode;
        String PARAMS = ORI+"&"+DES+"&"+MODE;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+PARAMS+"&key="+ getString (R.string.google_maps_key);
    }

    @Override
    public void onDirectionsRetrieved (Object... values) {
            if(polyline!=null)
                polyline.remove ();
            polyline = mMap.addPolyline ((PolylineOptions) values[0]);
    }
}