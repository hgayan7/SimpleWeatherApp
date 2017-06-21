package com.homework.him.simpleweatherapp;

import android.animation.AnimatorSet;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText searchEditText;
    ImageButton searchButton;
    ImageView weatherImage;
    TextView cityText, tempText, pressureText, descText,placeText,countryText;
    String searchText;
    CardView cardView;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        cityText = (TextView) findViewById(R.id.cityName);
        tempText = (TextView)findViewById(R.id.temperatureText);
        pressureText=(TextView)findViewById(R.id.pressureText);
        descText=(TextView)findViewById(R.id.desciptionText);
        weatherImage=(ImageView)findViewById(R.id.weatherImage);
        placeText=(TextView)findViewById(R.id.placeName);
        countryText=(TextView)findViewById(R.id.countryName);
        cardView=(CardView)findViewById(R.id.cardView);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        cardView.setVisibility(View.GONE);
        weatherImage.setVisibility(View.INVISIBLE);
        final Animation moveUp= AnimationUtils.loadAnimation(this,R.anim.acc_decc_animation);
        final Animation linearMoveUp=AnimationUtils.loadAnimation(this,R.anim.linear_move_up);
        checkforConnection();
        searchButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkforConnection()==false){
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
            }else {

               try {

                   searchText = searchEditText.getText().toString();
                   hideKeyboard();
                   GetWeatherInfo getWeatherInfo = new GetWeatherInfo();
                   //Use your own API key
                   getWeatherInfo.execute("http://api.openweathermap.org/data/2.5/weather?q="
                           + searchText + "&units=metric&APPID=Your Api Key ");
                   cardView.setVisibility(View.VISIBLE);
                   cardView.startAnimation(moveUp);
                   linearLayout.startAnimation(linearMoveUp);

               }
               catch (Exception e){
                   Toast.makeText(MainActivity.this, "Enter a valid location", Toast.LENGTH_SHORT).show();
               }
            }

        }
    });
    }
    //checking for network connectivity
    public boolean checkforConnection(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        boolean isConnected=(null!=networkInfo)&&(networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
    //hiding virtual keyboard
    public void hideKeyboard(){
        View view=this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    //background thread
    public class GetWeatherInfo extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String weatherData = "";
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char curr = (char) data;
                    weatherData = weatherData + curr;
                    data = inputStreamReader.read();
                }
                return weatherData;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String weatherData) {
            String description = null, temperature = null, pressure = null,placeName=null,countryName = null;
            try {
                JSONObject globalObject = new JSONObject(weatherData);

                    JSONArray jsonArray = new JSONArray(globalObject.getString("weather").toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject weatherObject = jsonArray.getJSONObject(i);
                            description = weatherObject.getString("main");
                        }
                    JSONObject mainObject = new JSONObject(globalObject.getString("main"));
                    temperature = mainObject.getString("temp");
                    pressure = mainObject.getString("pressure");
                    placeName = globalObject.getString("name");
                    JSONObject sysObject=new JSONObject(globalObject.getString("sys"));
                    countryName=sysObject.getString("country");

                
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (description.equals("Clear")) {
                weatherImage.setImageResource(R.drawable.sunny_icon);
            } else if (description.equals("Mist")) {
                weatherImage.setImageResource(R.drawable.mist_icon);

            } else if (description.equals("Rain")) {
                weatherImage.setImageResource(R.drawable.rain_icon);
            } else if (description.equals("overcast clouds")) {
                weatherImage.setImageResource(R.drawable.cloudy_icon);
            } else if (description.equals("Clouds")) {
                weatherImage.setImageResource(R.drawable.cloudy_icon);
            }
            else if (description.equals("Fog")) {
                weatherImage.setImageResource(R.drawable.mist_icon);
            }else {
                weatherImage.setImageResource(R.drawable.else_icon);
            }
            cityText.setText(searchText);
            placeText.setText(placeName);
            countryText.setText(countryName);
            descText.setText(description);
            tempText.setText(temperature + " °C");
            pressureText.setText(pressure + " hPa");
            weatherImage.setVisibility(View.VISIBLE);


        }




    }


}