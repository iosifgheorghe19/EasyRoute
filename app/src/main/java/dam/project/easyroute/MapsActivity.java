package dam.project.easyroute;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static TreeMap <Integer, Marker> treeMapMarkere = new TreeMap<Integer, Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //developer.android.com/training/location/retrievecurrent.html
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        SearchView searchView = (SearchView)findViewById(R.id.mapSearchView1);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchOnMap(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchStatii(newText);
                        return false;
                    }
                }
        );


    }


    public void searchOnMap(String query){



        List<Address> addressList = null;

        if(query!= null && !query.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {

                for( Marker m : treeMapMarkere.values()){
                    if(m.getSnippet().contains(query) || m.getTitle().contains(query))
                        m.setVisible(true);
                    else
                        m.setVisible(false);
                }
            }
            catch (Exception ex){
                Log.d("statii", ex.getMessage());
            }
            try {
                 addressList = geocoder.getFromLocationName(query,1);


            } catch (IOException e) {
                e.printStackTrace();
            }
            Address adresa = addressList.get(0);
            LatLng latLng = new LatLng(adresa.getLatitude(), adresa.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(adresa.getAddressLine(0)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        }
    }

    public void searchStatii(String query){
       try {

           for( Marker m : treeMapMarkere.values()){
               if(m.getSnippet().contains(query) || m.getTitle().contains(query))
                   m.setVisible(true);
               else
                   m.setVisible(false);
           }
       }
       catch (Exception ex){
           Log.d("statii", ex.getMessage());
       }

    }




    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
       //retinem locatia curenta:
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng latlongCurrent = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


       //     mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus1)).position(latlongCurrent).flat(true).title("Bucharest").snippet("aici sunt eu"));
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlongCurrent, 14));  // 0 = 0 zoom in.


            JSONParser parser = new JSONParser();
            parser.incepeParsareJSON(mMap);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        mMap.getUiSettings().isMapToolbarEnabled();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LinearLayout linearLayoutAutobuze = (LinearLayout)findViewById(R.id.autobuzeLayout);
                if(linearLayoutAutobuze.getChildCount() > 0)
                    linearLayoutAutobuze.removeAllViews();
             //   linearLayoutAutobuze.setMinimumHeight(100);

                SearchView searchView = (SearchView)findViewById(R.id.mapSearchView1);
                searchView.setIconified(true);

                String autobuze = marker.getSnippet().substring(marker.getSnippet().lastIndexOf(":")+1);
                ArrayList<String> listaAutobuze = Utile.convertStringToArrayList(autobuze);
                for( String s : listaAutobuze){
                    TextView tv = new TextView(getApplicationContext());
                    tv.setTextSize(16);
                    tv.setTextColor(Color.BLACK);
                    tv.setPadding(10, 10, 10, 10);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    tv.setText(s);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView t = (TextView)v;
                            int childcount = ((LinearLayout)t.getParent()).getChildCount();
                            for (int i=0; i < childcount; i++){
                                View v1 = ((LinearLayout)t.getParent()).getChildAt(i);
                                ((TextView)v1).setTextColor(Color.BLACK);
                                ((TextView)v1).setTypeface(null, Typeface.NORMAL);
                            }
                            t.setTextColor(Color.RED);
                            t.setTypeface(null, Typeface.BOLD);
                            searchStatii(t.getText().toString());
                        }
                    });
                    linearLayoutAutobuze.addView(tv);
                }

                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LinearLayout linearLayoutAutobuze = (LinearLayout) findViewById(R.id.autobuzeLayout);
                if (linearLayoutAutobuze.getChildCount() > 0)
                    linearLayoutAutobuze.removeAllViews();
             //   linearLayoutAutobuze.setMinimumHeight(0);
                searchStatii("");
            }
        });

        // Add a marker in Bucharest and move the camera
//        LatLng bucharest = new LatLng(44.4268, 26.1025);
//        mMap.addMarker(new MarkerOptions().position(bucharest).title("Bucharest"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(bucharest));

//        mMap.setMyLocationEnabled(true);

    }
}
