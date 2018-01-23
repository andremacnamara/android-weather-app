package andremacnamara.com.macnamaraweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import andremacnamara.com.macnamaraweather.Extras.DayAdapter;
import andremacnamara.com.macnamaraweather.models.Day;
import butterknife.ButterKnife;
import butterknife.BindView;

//Display upcoming week data
public class DailyForecastActivity extends Activity {

    private Day[] mDays;
    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(TodaysWeather.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapter adapter = new DayAdapter(this, mDays);

        mListView.setAdapter( adapter );
        mListView.setEmptyView( mEmptyTextView );
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayOfTheWeek = mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();
                String highTemp = mDays[position].getTemperatureMax() + "";
                String message = String.format("On %s the high will be %s and it will be %s",
                        dayOfTheWeek,
                        highTemp,
                        conditions);
                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        } );

        Animation animation;
        Animation animation1;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        mListView.startAnimation(animation);
        mListView.startAnimation(animation1);

    }

}









