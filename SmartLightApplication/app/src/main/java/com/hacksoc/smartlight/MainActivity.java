package com.hacksoc.smartlight;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView sensorReading, bulb;
    private ToggleButton onOffButton;
    private String jsonURLName = "https://api.pripoj.me/message/get/0004A30B001EE27F?token=84YTv1eXGqfwcZikLYkqWzR4aoMAyqDZ";
    private InputStream input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Beginning", "I got to the beginning!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorReading = (TextView)findViewById(R.id.temperature);
        onOffButton = (ToggleButton)findViewById(R.id.toggleButton);
        onOffButton.setOnClickListener(this);
        bulb = (TextView)findViewById(R.id.bulb);

        CountDownTimer newTimer = new CountDownTimer(1000000000, 4000) {
            public void onTick(long millisUntilFinished) {
                new ReadTemperatures().execute();
            }
            public void onFinish() {
            }
        }.start();
    }

    public void onClick(View v) {
        if(v == onOffButton) {
            RestAdapter retrofit = new RestAdapter.Builder()
                    .setEndpoint("https://api.particle.io")
                    .build();
            Microcontroller micro = retrofit.create(Microcontroller.class);

            if(onOffButton.isChecked()) {
                bulb.setText("Off");
                micro.off(
                        "off",
                        new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response) {
                                BufferedReader reader = null;
                                String output = "";

                                try {
                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                    output = reader.readLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
            else {
                bulb.setText("On");
                micro.on(
                        "on",
                        new Callback<Response>() {
                            @Override
                            public void success(Response result, Response response) {
                                BufferedReader reader = null;
                                String output = "";

                                try {
                                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                                    output = reader.readLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        }
    }

    class ReadTemperatures extends AsyncTask<Void, Void, Void> {
        InputStream inputStream = null;
        String result = "start";

        @Override
        protected void onPreExecute() {
            Log.d("Execute", "I got to pre execute!");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Bg", "I got to background!");

            try {

                URL jsonURL = new URL("https://api.pripoj.me/message/get/0004A30B001EE27F?token=84YTv1eXGqfwcZikLYkqWzR4aoMAyqDZ&limit=1000");
                InputStream inputStream = jsonURL.openStream();

                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();
                Log.d("Result", result);

            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }

            try {
                Constructor<Void> constructor = Void.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
            catch(Exception e) {
                Log.e("getDeclaredConstructor & newInstance", "Some failure" + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            //parse JSON data
            Log.d("End", "I got to end!");

            try {

                JSONObject jObj = new JSONObject(result);
                JSONArray jArray = jObj.getJSONArray("records");
                JSONObject lastObj = jArray.getJSONObject(0);
                String hexResult = lastObj.getString("payloadHex");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < hexResult.length(); i += 2) {
                    String str = hexResult.substring(i, i + 2);
                    result.append((char)Integer.parseInt(str, 16));
                }
                sensorReading.append("Current room temperature: " + result + "\u00B0 C\n");
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
        }
    }
}

