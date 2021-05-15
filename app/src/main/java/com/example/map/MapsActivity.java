package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SQLiteDB db;
    private GoogleMap mMap;
    private Button btnClr;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnClr = findViewById(R.id.btnClr);
        spinner = findViewById(R.id.spinner);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        db = new SQLiteDB(this);
        //db.onUpgrade(db.getReadableDatabase(), 1, 1);
        for (Coord coord : db.getAllCoord()) {
            System.out.println(coord);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(coord.getLat(), coord.getLon())).draggable(true).zIndex(coord.getId()).visible(false));
            runThread(marker, marker.getPosition());
            System.out.println(marker.getPosition());
        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng  coord) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(coord).draggable(true).visible(false));
                assert marker != null;
                runThread(marker, marker.getPosition());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                int id = db.addCoord(new Coord(marker.getPosition().latitude, marker.getPosition().longitude));
                marker.setZIndex(id);
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                System.out.println(marker.getZIndex());
                db.deleteCoord(db.getCoord((int) marker.getZIndex()));
                marker.remove();
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                marker.hideInfoWindow();
            }
        });
        btnClr.setOnClickListener(view -> {
            if (db.getCoordCount() == 0)
                return;
            db.onUpgrade(db.getReadableDatabase(), 1, 1);
            googleMap.clear();
        });

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.typeMap, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    private void runThread(Marker marker, LatLng coord) {

        new Thread() {
            public void run() {
                try {
                    URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + coord.latitude + "&lon=" + coord.longitude + "&appid=11a63413e00ad38ac2fc4fc5b4a2e8f2");
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("GET");
                    http.setRequestProperty("Content-length", "0");
                    http.setUseCaches(false);
                    http.setAllowUserInteraction(false);
                    http.connect();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    String s = sb.toString();
                    JSONObject jsonObject = new JSONObject(s);
                    Weather weather = new Weather(jsonObject);

                    runOnUiThread(() -> {
                        if (weather.getCity().equals(""))
                            marker.setTitle("\"" + weather.getLat() + ", " + weather.getLon() + "\" [ " + weather.getTempStr() + "°C ] (" + weather.getDescription().toUpperCase() + ")");
                        else
                            marker.setTitle(weather.getCity() + " [ " + weather.getTempStr() + "°C ] (" + weather.getDescription().toUpperCase() + ")");
                        marker.setSnippet(weather.getTime().toString());
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Weather.createImage(weather.getTemp(), getResources())));
                        marker.setVisible(true);
                    });

                } catch (IOException | JSONException err) {
                    System.err.println(err);
                }
            }

        }.start();
    }
}
