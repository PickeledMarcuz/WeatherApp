package io.markryan.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    String urlFinal = "http://api.openweathermap.org/data/2.5/weather?id={TOKEN}";
    EditText cityNameEditText;
    TextView resultTextView;

    public void findWeather(View view) {
        Log.i("City Name", cityNameEditText.getText().toString());

        // To manage hiding the keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityNameEditText.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityNameEditText.getText().toString(), "UTF-8");
            DownloadManager task = new DownloadManager();
            task.execute(urlFinal+encodedCityName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "City name doesnt seem right", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameEditText = (EditText) findViewById(R.id.cityNameEditText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);


    }

    public class DownloadManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }
                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject;
            JSONObject weatherObj = null;
            String weatherInfo,
                    message = "";

            try {
                jsonObject = new JSONObject(s);
                weatherInfo = jsonObject.getString("weather");

                Log.i("weather info", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++){
                    weatherObj = arr.getJSONObject(i);

                    String main = weatherObj.getString("main");
                    String description = weatherObj.getString("description");

                    if (main != "" && description != ""){
                        message += main + ": " + description + "\r\n";
                    }
                    if (message != ""){
                        resultTextView.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();

                    }

                    /* LOGS FOR TESTING
                    Log.i("main", weatherObj.getString("main"));
                    Log.i("description", weatherObj.getString("description"));
                    */
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}
