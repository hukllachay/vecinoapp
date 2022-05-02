package com.grupo1.vecinoapp.Location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;


import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AsyncTask for reverse geocoding coordinates into a physical address.
 */
public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private Context mContext;
    private OnTaskCompleted mListener;
    private PreferencesManager session_management;


    public FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
        session_management = new PreferencesManager(applicationContext);
    }

    private final String TAG = FetchAddressTask.class.getSimpleName();

    @Override
    protected String doInBackground(Location... params) {
        // Set up the geocoder
        Geocoder geocoder = new Geocoder(mContext,
                Locale.getDefault());

        // Get the passed in location
        Location location = params[0];
        List<Address> addresses = null;
        String resultMessage = "";

        try {

//            addresses = geocoder.getFromLocation(
//                    location.getLatitude(),
//                    location.getLongitude(),
//                    // In this sample, get just a single address
//                    1);
            addresses = getAddress(location);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
//            resultMessage = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // If no addresses found, print an error message.
        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
//                resultMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join(
                    "\n",
                    addressParts);

        }

        return resultMessage;
    }

    /**
     * Called once the background thread is finished and updates the
     * UI with the result.
     * @param address The resulting reverse geocoded address, or error
     *                message if the task failed.
     */
    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    private List<Address> getAddress(Location location) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());
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
            String city = addresses.get(0).getLocality();

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            String address = addresses.get(0).getAddressLine(0);
            String knownName = addresses.get(0).getSubAdminArea();

            session_management.setLocationCity(city);
            session_management.setLocationPref(String.valueOf(latitude), String.valueOf(longitude));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return addresses;
    }

   public interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}