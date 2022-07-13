package com.mobio.sample;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobio.analytics.client.inapp.htmlPopup.JavaScriptInterface;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {

    LocationManager locationManager;
    String provider;
    private Dialog dialog;


    private final String templateHtml = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=0\">\n" +
            "    <style>\n" +
            "                html {\n" +
            "                    height: 100%;\n" +
            "                }\n" +
            "                body {\n" +
            "                    min-height: 100%;\n" +
            "                    position: relative;\n" +
            "                    background: rgba(0,0,0,.2);\n" +
            "                }\n" +
            "                #m_modal {\n" +
            "                    width: 100%;\n" +
            "                    position: absolute;\n" +
            "                    transform: translate(-50%, -50%);\n" +
            "                    top: 50%;\n" +
            "                    left: 50%;\n" +
            "                    right: auto;\n" +
            "                    bottom: auto;\n" +
            "                    text-align: center;\n" +
            "                    border-radius: 10px;\n" +
            "                    background: #fff;\n" +
            "                }\n" +
            "                @media (min-width: 576px) {\n" +
            "                    #m_modal {\n" +
            "                    max-width: 300px;\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                @media (min-width: 576px) {\n" +
            "                    #m_modal {\n" +
            "                    max-width: 500px;\n" +
            "                    margin: 1.75rem auto;\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                #m_modal img {\n" +
            "\t                width: -webkit-fill-available !important;\n" +
            "                }\n" +
            "\n" +
            "\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div id=\"m_modal\">\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    private final String HTML_MIME_TYPE = "text/html";
    private final String HTML_ENCODING = "utf-8";
    private static final int ID_OF_URL_BAR = 112233;
    private static final int ID_OF_URL_TEXTVIEW = 332211;
    private static final int ID_OF_URL_LINE = 222211;
    public static final String M_KEY_PUSH = "m_key_push";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
        // Getting LocationManager object
        statusCheck();

        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
            // Get the location from the given provider
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 500, 0, this);

            if (location != null)
                onLocationChanged(location);
            else
                location = locationManager.getLastKnownLocation(provider);
            if (location != null)
                onLocationChanged(location);
            else

                Toast.makeText(getBaseContext(), "Location can't be retrieved",
                        Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_webview, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        WebView webView = popupView.findViewById(R.id.wv_popup);
        webView.loadUrl("https://mobio.io/");

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* getMenuInflater().inflate(R.menu.activity_main, menu); */
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Getting reference to TextView tv_longitude
        TextView tvLongitude = (TextView) findViewById(R.id.tv_longitude);

        // Getting reference to TextView tv_latitude
        TextView tvLatitude = (TextView) findViewById(R.id.tv_latitude);

        // Setting Current Longitude
        tvLongitude.setText("Longitude:" + location.getLongitude());

        // Setting Current Latitude
        tvLatitude.setText("Latitude:" + location.getLatitude());

        getAddress(location.getLatitude(), location.getLongitude());
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();

                String finalAdd = add;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvAddress = (TextView) findViewById(R.id.tv_location);
                        tvAddress.setText(finalAdd);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}