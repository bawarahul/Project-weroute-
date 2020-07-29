package obid.weroute;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener,
        LocationListener {
    private GoogleMap mMap;
    SupportMapFragment sMapFragment;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    int route=0;
    Layout l1;
    String GOOGLE_API_KEY="AIzaSyDWBlnKDBkYPMWgfrzshapiv3wCNsIwAz4";
    private Marker currentLocationmMarker,selectedlocationmarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 100000;
    LatLng latlng;
    int camStatus=0;
    ArrayAdapter adapter;
    Intent intent;
    EditText e1;
    Button b1;
    int lvisible=0,selected=0;
    ListView listView;
    private int REQUEST_CODE_PLACEPICKER = 1;
    double latitude,longitude,wlat=0,wlong=0;
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        sMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        sMapFragment.getMapAsync(this);
        e1=(EditText)findViewById(R.id.editText3);
        b1=(Button)findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(e1.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Enter Something in Search field ",Toast.LENGTH_SHORT).show();
                }
                else{
                    Object[] DataTransfer = new Object[2];
                    String url = getUrl(latitude, longitude,e1.getText().toString().replace(" ","_"));

                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(getApplicationContext(),"Searching", Toast.LENGTH_LONG).show();
                }
            }
        });
        intent=new Intent(MainActivity.this,WeatherActivity.class);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==0){
                    Snackbar.make(view, "Select a marker first", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    selected=0;
                    intent.putExtra("latitude",String.valueOf(wlat));
                    intent.putExtra("longitude",String.valueOf(wlong));
                    startActivity(intent);
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Changing Camera View", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(camStatus==0){
                CameraPosition cpos= new CameraPosition.Builder().target(latlng).zoom(17).bearing(90).tilt(45).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cpos));
                    camStatus=1;
            }
            else{
                    CameraPosition cpos= new CameraPosition.Builder().target(latlng).zoom(15).bearing(0).tilt(0).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cpos));
                    camStatus=0;
            }
            }
        });
        listView = (ListView) findViewById(R.id.marker_details);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.action_settings:
                sFm.beginTransaction().hide(sMapFragment).commit();


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Object[] DataTransfer = new Object[2];
        if (id == R.id.nav_camera) {

            if (!sMapFragment.isAdded())
                sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
            else
                sFm.beginTransaction().show(sMapFragment).commit();
            // Handle the camera action
        } else if (id == R.id.school) {
            mMap.clear();
            if (currentLocationmMarker != null) {
                currentLocationmMarker.remove();
            }
            String url = getUrl(latitude, longitude,"school" );

            DataTransfer[0] = mMap;
            DataTransfer[1] = url;
            Log.d("onClick", url);
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
            Toast.makeText(getApplicationContext(),"Nearby Schools", Toast.LENGTH_LONG).show();

        } else if (id == R.id.restaurants) {
            if (sMapFragment.isAdded()) {
                mMap.clear();
                if (currentLocationmMarker != null) {
                    currentLocationmMarker.remove();
                }
                String url = getUrl(latitude, longitude,"restaurant" );

                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(getApplicationContext(),"Nearby Restaurants", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.atm) {
            if (sMapFragment.isAdded()) {
                if (sMapFragment.isAdded()) {
                    mMap.clear();
                    if (currentLocationmMarker != null) {
                        currentLocationmMarker.remove();
                    }
                    String url = getUrl(latitude, longitude,"atm" );

                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(getApplicationContext(),"Nearby ATMs", Toast.LENGTH_LONG).show();
                }
            }

        } else if (id == R.id.mall) {
                    if (sMapFragment.isAdded()) {
                        mMap.clear();
                        if (currentLocationmMarker != null) {
                            currentLocationmMarker.remove();
                        }
                        String url = getUrl(latitude, longitude,"shopping_mall" );

                        DataTransfer[0] = mMap;
                        DataTransfer[1] = url;
                        Log.d("onClick", url);
                        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                        getNearbyPlacesData.execute(DataTransfer);
                        Toast.makeText(getApplicationContext(),"Nearby Shopping Malls", Toast.LENGTH_LONG).show();
                    }


        } else if (id == R.id.get_weather) {
            intent.putExtra("latitude",String.valueOf(latitude));
            intent.putExtra("longitude",String.valueOf(longitude));
            startActivity(intent);
        }
        else if(id == R.id.nav_send){
            Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1LbJleTCd4h_xBnykZ749lk5_U2q0EtqvRRP7R8Dfl1c/"));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
       double returnLat(){
           return latitude;
       }
       double returnLog(){
           return longitude;
       }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                       wlat=marker.getPosition().latitude;
                       wlong=marker.getPosition().longitude;
                    selected=1;
                    return false;
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng loc) {
                    Log.d("DEBUG","Map clicked [" + loc.latitude + " / " + loc.longitude + "]");
                    LatLng markerpos = new LatLng(loc.latitude , loc.longitude);
                    Toast.makeText(getApplicationContext(),"click marker to get its weather details",Toast.LENGTH_SHORT).show();
                    if(selectedlocationmarker != null)
                    {
                        selectedlocationmarker.remove();

                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(markerpos);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    selectedlocationmarker=mMap.addMarker(markerOptions);
                }

            });
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        latlng=latLng;
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }



    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(currentLocationmMarker))
        {

            return false;
        }
        else{

            return true;
        }
    }

 
}
