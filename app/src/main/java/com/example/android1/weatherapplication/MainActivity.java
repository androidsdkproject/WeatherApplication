package com.example.android1.weatherapplication;
import java.util.List;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.android1.weatherapplication.SplashActivity.PERMISSION_ALL;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    Typeface weatherFont;
    String lat;
    String longi;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    String TAG="Main";
    GPSTracker gps;
    ProgressBar progressBar;
    String city_name_for_weather="Kota Rajasthan";
    EditText getcity;
    String[] PERMISSIONS={
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_GSERVICES

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getcity=(EditText)findViewById(R.id.city);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);



        this.findViewById(R.id.currentlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissions(MainActivity.this, PERMISSIONS))
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);

                gps = new GPSTracker(MainActivity.this);


                if(gps.canGetLocation()){

                    lat = String.valueOf(gps.getLatitude());
                    longi = String.valueOf(gps.getLongitude());
                    Log.d(TAG,lat+longi+"");
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat+ "\nLong: " + longi, Toast.LENGTH_LONG).show();
                    callWeather();
                }else{
                    gps.showSettingsAlert();
                }




            }
        });




        this.findViewById(R.id.citybutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                city_name_for_weather=getcity.getText().toString();
                if(city_name_for_weather!=null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getcity.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    getLocationFromAddress(getApplicationContext(),city_name_for_weather);
                    callWeather();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                 getcity.setError("enter city");
                }

            }
        });


        this.findViewById(R.id.Refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"refresh",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
                callWeather();
                progressBar.setVisibility(View.GONE);
            }
        });


        this.findViewById(R.id.menu_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });


        getLocationFromAddress(getApplicationContext(),city_name_for_weather);
        callWeather();
        progressBar.setVisibility(View.GONE);

    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    void callWeather() {

        WeatherInfo.placeIdTask asyncTask = new WeatherInfo.placeIdTask(new WeatherInfo.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: " + weather_humidity);
                pressure_field.setText("Pressure: " + weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute(lat, longi); //  asyncTask.execute("Latitude", "Longitude")
    }

    public void getLocationFromAddress(Context context, String strAddress) {

        if (strAddress.equals(""))
            strAddress = "jaipur";

        Geocoder coder = new Geocoder(context);
        List<Address> address;


        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);

            Address location = address.get(0);
            lat = String.valueOf(location.getLatitude());
            longi = String.valueOf(location.getLongitude());


        }
        catch (IndexOutOfBoundsException ex)
        {


        }
        catch (IOException ex) {

            ex.printStackTrace();
        }


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Help:
                Toast.makeText(getApplicationContext(), "Help", Toast.LENGTH_LONG).show();
                return true;
            case R.id.Settings:
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.About:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_LONG).show();
                return true;
            case R.id.Exit:
                Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_LONG).show();
                finish();
                return true;
            default:
                return false;
        }


    }


    public boolean hasPermissions(Context context, String... permissions) {


        if (android.os.Build.VERSION.SDK_INT >= 21 && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }



        return true;
    }




}







