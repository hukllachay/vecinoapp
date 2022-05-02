package com.grupo1.vecinoapp.Location;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.internal.LatLngAdapter;
import com.google.maps.model.GeocodingResult;
import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;
import com.grupo1.vecinoapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;



public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, LocationListener, FetchAddressTask.OnTaskCompleted, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "TAG";
    private static final int REQUEST_LOCATION_PERMISSION = 124;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 3000;
    boolean canGetLocation = false;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private GoogleMap mMap;
    private MultiTouchMapFragment mapFragment;
    private ImageView back_btn;
    private FusedLocationProviderClient mFusedLocationClient;
    //private Session_management session_management;
    private LocationRequest locationRequest;
    private int mInterval = 3;
    private int nInterval = 1;
    private Location location;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private RecyclerView search_view_recy;
    private EditText search_text;
    private LinearLayout search_lay;
    private LinearLayout address_lay;
    private TextView address_text;
    private TextView save_loc;

    private Handler handler = new Handler();
    private Gson gson = new GsonBuilder().registerTypeAdapter(LatLng.class, new LatLngAdapter())
            .create();
    private RequestQueue queue;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private PlacePredictionAdapter adapter;

    private ViewAnimator viewAnimator;
    private ProgressBar progressBar;
    private String address = "";
    private boolean inPlacePredection = false;
    private PreferencesManager pref;
   // String uid = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        try {
            //uid = getIntent().getStringExtra("LUID");
            pref = new PreferencesManager(this);
            Places.initialize(getApplicationContext(), getResources().getString(R.string.map_api_key));
            back_btn = findViewById(R.id.back_btn);
            search_view_recy = findViewById(R.id.search_view_recy);
            search_text = findViewById(R.id.search_txt);
            search_lay = findViewById(R.id.search_lay);
            progressBar = findViewById(R.id.progressbar);
            address_text = findViewById(R.id.address_text);
            address_lay = findViewById(R.id.address_lay);
            save_loc = findViewById(R.id.save_loc);
            search_lay.setVisibility(View.GONE);
            placesClient = Places.createClient(this);
            queue = Volley.newRequestQueue(this);
            back_btn.setOnClickListener(v -> onBackPressed());
            //session_management = new Session_management(AddressLocationActivity.this);
//          geoDataClient = com.google.android.libraries.places.compat.Places.getGeoDataClient(AddressLocationActivity.this);
//          geoDataClient = Places.getGeoDataClient(AddressLocationActivity.this);
//          mPlaceDetectionClient = Places.getPlaceDetectionClient(AddressLocationActivity.this);
            mapFragment = (MultiTouchMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            configureCameraIdle();
            if (checkAndRequestPermissions()) {
                getLocationRequest();
            }
            /*  LatLngBounds bounds = new LatLngBounds(new LatLng(7.2, 67.8), new LatLng(36.5, 93.8));
            nAdapter = new PlaceAutocompleteAdapter(AddressLocationActivity.this, R.layout.search_place,
            null, bounds, null, this, geoDataClient);
            search_view_recy.setAdapter(nAdapter);
*/
            save_loc.setOnClickListener(v -> onBackPressed());

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            search_view_recy.setLayoutManager(layoutManager);
            adapter = new PlacePredictionAdapter(this::geocodePlaceAndDisplay);
            search_view_recy.setAdapter(adapter);
            search_view_recy.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
            adapter.setPlaceClickListener(this::geocodePlaceAndDisplay);
            search_text.setOnClickListener(v -> sessionToken = AutocompleteSessionToken.newInstance());

            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (search_text.getText().toString() != null && search_text.getText().toString().length() > 0) {
                        search_lay.setVisibility(View.VISIBLE);
                        getPlacePredictions(search_text.getText().toString());
//                    nAdapter.getFilter().filter(search_text.getText().toString());
                    } else {
                        search_lay.setVisibility(View.GONE);
                    }

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void getPlacePredictions(String query) {

        // The value of 'bias' biases prediction results to the rectangular region provided
        // (currently Kolkata). Modify these values to get results for another area. Make sure to
        // pass in the appropriate value/s for .setCountries() in the
        // FindAutocompletePredictionsRequest.Builder object as well.
        final LocationBias bias = RectangularBounds.newInstance(
                new LatLng(7.2, 67.8), // SW lat, lng
                new LatLng(36.5, 93.8) // NE lat, lng
        );

//        LatLngBounds bounds = new LatLngBounds(new LatLng(7.2, 67.8), new LatLng(36.5, 93.8));

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(sessionToken)
                //.setLocationBias(bias)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setQuery(query)
                //.setCountries("IN")
                .build();

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            adapter.setPredictions(predictions);

           //progressBar.setIndeterminate(false);
          // viewAnimator.setDisplayedChild(predictions.isEmpty() ? 0 : 1);
        }).addOnFailureListener((exception) -> {
//            progressBar.setIndeterminate(false);
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void geocodePlaceAndDisplay(AutocompletePrediction placePrediction) {
        Log.e(TAG, "geocodePlaceAndDisplay: "+placePrediction );
        search_text.setText("");
        inPlacePredection = true;
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        search_lay.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        // Construct the request URL
        final String apiKey = getResources().getString(R.string.map_api_key);
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?place_id="+placePrediction.getPlaceId()+"&key="+apiKey;
        final String requestURL = String.format(url, placePrediction.getPlaceId(), apiKey);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Inspect the value of "results" and make sure it's not empty
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() == 0) {
                            Log.e("TAG", "No results from geocoding request.");
                            return;
                        }

                        // Use Gson to convert the response JSON object to a POJO
                        GeocodingResult result = gson.fromJson(results.getString(0), GeocodingResult.class);
                        Log.i("TAG", result.toString());
                        Location locations = new Location("point 1");
                        locations.setLatitude(result.geometry.location.lat);
                        locations.setLongitude(result.geometry.location.lng);
                        location = locations;
                        mMap.clear();
                        pref.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        getAddress();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                }, error -> {
            progressBar.setVisibility(View.GONE);
            Log.e("TAG", "Request failed");
        });

        // Add the request to the Request queue.
        queue.add(request);
    }

    private void displayDialog(AutocompletePrediction place, GeocodingResult result) {
        new AlertDialog.Builder(this)
                .setTitle(place.getPrimaryText(null))
                .setMessage("Geocoding result:\n" + result.geometry.location + "\n" + place.getSecondaryText(null) + "\n" + result.formattedAddress + "\n" + result.types)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Location locations = new Location("point 1");
                        locations.setLatitude(result.geometry.location.lat);
                        locations.setLongitude(result.geometry.location.lng);
                        location = locations;
                        mMap.clear();
                        pref.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //new FetchAddressTask(AddressLocationActivity.this, AddressLocationActivity.this).execute(location);
                        getAddress();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setSupLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    pref.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    mMap.clear();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                  new FetchAddressTask(AddressLocationActivity.this, AddressLocationActivity.this).execute(location);
                    getAddress();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//      LatLng sydney = new LatLng(28.992845, 77.016551);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(false);
        mapFragment.mTouchView.setGoogleMap(mMap);
        if (!pref.getLatPref().equalsIgnoreCase("") && pref.getLangPref().equalsIgnoreCase("")) {
            LatLng latLng = new LatLng(Double.parseDouble(pref.getLatPref()), Double.parseDouble(pref.getLangPref()));
            mMap.addMarker(new MarkerOptions().position(latLng));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        } else {
            //comment by me
            /* LatLngBounds INDIA = new LatLngBounds(new LatLng(7.2, 67.8), new LatLng(36.5, 93.8));
            mMap.addMarker(new MarkerOptions().position(INDIA.getCenter()));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(INDIA.getCenter()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA.getCenter(), 11));
     */
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(22);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location locations) {
        if (!inPlacePredection) {
            if (locations != null) {
                Log.e("TAG", "onLocationChanged: " + locations.getLatitude() + "\n" + locations.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.getLatitude(), locations.getLongitude()), 11));
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (!pref.getLatPref().equalsIgnoreCase("") && !pref.getLangPref().equalsIgnoreCase("")) {
                            DecimalFormat dFormat = new DecimalFormat("##.#######");
                            LatLng latLng = new LatLng(Double.parseDouble(pref.getLatPref()), Double.parseDouble(pref.getLangPref()));
                            double latitude = Double.valueOf(dFormat.format(latLng.latitude));
                            double longitude = Double.valueOf(dFormat.format(latLng.longitude));
                            Log.i("TAG", latitude + "\n" + longitude);
                            Location locationA = new Location("cal 1");
                            //Location locationB = new Location("cal 2");
                            locationA.setLatitude(latitude);
                            locationA.setLongitude(longitude);
                            //locationB = locations;
                            double disInMetter = locationA.distanceTo(locations);
                            double disData = disInMetter / 1000;
                            DecimalFormat dFormatt = new DecimalFormat("#.#");
                            disData = Double.parseDouble(dFormatt.format(disData));
                            Log.i("TAG", "in" + disData);
                            if (disData > 5.0) {
                                location = locations;
                                getAddress();
                            }
                        } else {
                            try {
                                mapFragment.getMapAsync(googleMap -> {
                                    mMap.clear();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.getLatitude(), locations.getLongitude()), 11));
                                    location = locations;
                                    getAddress();
                                });

                            } catch (Exception e) {
                                Log.e(TAG, "onLocationChanged: " + e.toString());
                            }
                        }
                    }
                }).start();
            } else {
                if (location == null) {
                    location = locations;
                    mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11));
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    getAddress();
                }
            }
        }
//        if (locations != null) {
//            this.location = locations;
//            if (session_management != null && mMap != null) {
//                session_management.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//                mMap.clear();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
//                getAddress();
//            }
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LocationActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private void getLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(mInterval * 1000);
        locationRequest.setFastestInterval(nInterval * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        location = getLocation();
        if (location != null) {
            if (pref != null && mMap != null) {
                // session_management.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                mMap.clear();
                Toast.makeText(LocationActivity.this, "Found", Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                getAddress();
            }
        } else {
            Toast.makeText(LocationActivity.this, "Location Null setiing", Toast.LENGTH_SHORT).show();

            setSupLocation();
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                setSupLocation();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onTaskCompleted(String result) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = getLocation();
                    if (location != null) {
                        if (mMap != null) {
                            //session_management.setLocationPref(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                            mMap.clear();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                            mMap.addMarker(new MarkerOptions().position(latLng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            getAddress();
                        }
                    } else {
                        setSupLocation();
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showDialogOK("Se requiere permiso de servicios de ubicación para esta aplicación",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                break;
                                        }
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {

                        //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkAndRequestPermissions() {

        int locationPermission = ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(LocationActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_LOCATION_PERMISSION);
            Toast.makeText(LocationActivity.this, "Se requiere permiso de servicios de ubicación para esta aplicación", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void getAddress() {
        new Thread(() -> {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            DecimalFormat dFormat = new DecimalFormat("#.######");
            double latitude = Double.parseDouble(dFormat.format(location.getLatitude()));
            double longitude = Double.parseDouble(dFormat.format(location.getLongitude()));

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String city = addresses.get(0).getSubLocality();
                pref.setLocationCity(city);
                pref.setLocationPref(String.valueOf(latitude), String.valueOf(longitude));
                //Log.e("TAG", "strReturnedAddress:" + strReturnedAddress.toString());
                //Log.e("TAG", "returnedAddress:" + returnedAddress.toString());
                address = returnedAddress.getAddressLine(0);
                //Log.e(TAG, "getAddress:ba "+addresses );
                runOnUiThread(() -> {
                    if (inPlacePredection) {
                        if (!address.equalsIgnoreCase("")) {
                            address_lay.setVisibility(View.VISIBLE);
                            address_text.setText(address);
                            pref.setLocationCity(address);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }



    private void configureCameraIdle() {
        onCameraIdleListener = () -> {
            try {
                LatLng latLng = mMap.getCameraPosition().target;
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                inPlacePredection = true;
                Log.e("TAG", "Location: " + "" + location.getLatitude() + "" + location.getLongitude());
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(latLng));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                getAddress();
            } catch (Exception e) {
                Log.e(TAG, "configureCameraIdle: " + e.toString());
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}