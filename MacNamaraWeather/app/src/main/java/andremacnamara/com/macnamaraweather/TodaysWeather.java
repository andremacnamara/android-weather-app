package andremacnamara.com.macnamaraweather;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import andremacnamara.com.macnamaraweather.Extras.AlertDialogFragment;
import andremacnamara.com.macnamaraweather.models.Day;
import andremacnamara.com.macnamaraweather.models.Forecast;
import andremacnamara.com.macnamaraweather.models.TodayWeatherModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TodaysWeather extends AppCompatActivity {
    //Tag
    public static final String Tag = TodaysWeather.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";

    //Referencing the forecae model
    private Forecast mForecast;
    //Declaring labels using Butter Knife
    //Libary Reference
    /*
    * URL:https://github.com/jakewharton/butterknife
    * Author: Jake Wharton
    */
    @BindView(R.id.timeLabel)TextView mTimeLabel;
    @BindView(R.id.temperatureLabel)TextView mTempreatureLabel;
    @BindView(R.id.humidityValue)TextView mHumidityValue;
    //precipValue
    @BindView(R.id.precipValue)TextView mPrecipValue;
    //summaryLabel
    @BindView(R.id.summaryLabel)TextView mSummaryLabel;
    //iconImageView
    //@BindView(R.id.iconImageView)ImageView mIconImageView;
    @BindView(R.id.refreshImageView)ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        //ButterKnife Reerence
        ButterKnife.bind(this);

        //Progress Bar
        mProgressBar.setVisibility(View.INVISIBLE);

        //Doublin Co-ordinates
        final double latitude = 53.3498;
        final double longitude = -6.2603;

        //Refresh button
        mRefreshImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getForecast( latitude,longitude );
            }
        });

        //Getting th data
        getForecast(latitude, longitude);
        Log.d(Tag,"MAIN UI code running");

        //Animation fade for data comnig in
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        mTempreatureLabel.startAnimation(animation);
        mPrecipValue.startAnimation( animation );
        mHumidityValue.startAnimation( animation );
    }



    //Getting the weather data
    private void getForecast(double latitude, double longitude) {
        String apiKey = "b4c6147dc03f9cd198f270b6a8fa8484";
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey +  "/" + latitude + "," + longitude;

        //Checking network
        if(isNetworkAvaliable()) {
            toggleRefresh();

            //@https://stackoverflow.com/a/32915833

            //Using OkHTTP library
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url( forecastURL )
                    .build();

            Call call = client.newCall( request );
            call.enqueue( new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    } );

                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    } );
                    try {
                        String jsonData = response.body().string();
                        //Response response = call.execute();
                        Log.v( Tag, jsonData);
                        if (response.isSuccessful()) {
                            //Getting details
                            mForecast = getForecastDetails(jsonData);
                            runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            } );

                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e( Tag, "Exception caught: ", e );
                    }
                    catch (JSONException e){
                        Log.e(Tag, "Exception caught:",e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network is unavaliable!:",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Refresh button
    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility( View.VISIBLE );
            mRefreshImageView.setVisibility( View.INVISIBLE );
        } else {
            mProgressBar.setVisibility( View.INVISIBLE );
            mRefreshImageView.setVisibility( View.VISIBLE );
        }
    }

    //Display
    private void updateDisplay() {

        //Current weather model
        TodayWeatherModel current = mForecast.getmCurrent();

        mTempreatureLabel.setText(current.getmTemperature() + "");
        mTimeLabel.setText("At" + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getmHumidity() +"");
        mPrecipValue.setText(current.getmPrecipChance() + "%");
        mSummaryLabel.setText(current.getmSummary());
    }

    //Forecast weather model
    private Forecast getForecastDetails(String jsonData) throws JSONException{
        Forecast forecast = new Forecast();

        forecast.setmCurrent( getCurrentDetails(jsonData  ) );
        forecast.setmDailyForecast( getDailyForecast( jsonData ) );
        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }

        return days;
    }
    //Getting today/current details
    private TodayWeatherModel getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(Tag, "From Json:" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        TodayWeatherModel currentWeather = new TodayWeatherModel();

        currentWeather.setmHumidity(currently.getDouble("humidity"));
        currentWeather.setmTime(currently.getLong("time"));
        currentWeather.setmIcon(currently.getString("icon"));
        currentWeather.setmPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setmSummary(currently.getString("summary"));
        currentWeather.setmTemperature(currently.getDouble("temperature"));
        currentWeather.setmTimeZone(timezone);

        return currentWeather;
    }

    //Is network avaliale
    private boolean isNetworkAvaliable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvaliable = false;

        if(networkInfo != null &&networkInfo.isAvailable()){
            isAvaliable = true;
        }

        return isAvaliable;
    }

    //Error message
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    //Button for tomorrow
    @OnClick(R.id.todayButtonLabel)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getmDailyForecast());
        startActivity(intent);
    }



}
