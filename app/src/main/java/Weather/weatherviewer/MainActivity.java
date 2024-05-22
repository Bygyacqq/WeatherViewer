package Weather.weatherviewer;
import androidx.appcompat.app.AppCompatActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import android.support.*;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        weatherListView = (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter= new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText =(EditText) findViewById(R.id.LocationEditText);
                URL url =createURL(locationEditText.getText().toString());
                if(url !=null)
                {
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute();
                }
                else
                {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void dismissKeyboard(View view)
    {
        InputMethodManager Imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL createURL(String city)
    {
        String apikey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);
        try
        {
            String urlString = baseUrl + URLEncoder.encode(city, "UTF-8") + "&units=imperial&cnt=16&APPID=" + apikey;
            return new URL(urlString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    private class Weather {
        public Weather(long dt, double min, double max, double humidity, String description, String icon) {


        }
    }

    private class WeatherArrayAdapter implements ListAdapter {
        public WeatherArrayAdapter(MainActivity mainActivity, List<Weather> weatherList) {

        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public void notifyDataSetChanged() {
        }
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject>
    {
        public void execute() {
        }

        @Override
        protected JSONObject doInBackground(URL... params)
        {
            HttpURLConnection connection =null;
            try
            {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK)
                {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream())))
                    {
                        String line;
                        while ((line =reader.readLine())!=null)
                        {
                            builder.append(line);
                        }
                    }
                    catch (IOException e)
                    {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                }
                else
                {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error,Snackbar.LENGTH_LONG).show();

                }

            }
            catch (Exception e)
            {
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally
            {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject weather)
        {
            convertJSONtoArrayList(weather);
            weatherArrayAdapter.notifyDataSetChanged();
            weatherListView.smoothScrollToPosition(0);
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast)
    {
        weatherList.clear();
        try
        {
            JSONArray list = forecast.getJSONArray("list");
            for (int i = 0; i<list.length(); i++)
            {
                JSONObject day = list.getJSONObject(i);
                JSONObject temperatures = day.getJSONObject("temp");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                weatherList.add(new Weather(
                        day.getLong("dt"),
                        temperatures.getDouble("temp_min"),
                        temperatures.getDouble("temp_max"),
                        day.getDouble("humidity"),
                        weather.getString("description"),
                        weather.getString("icon")));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}