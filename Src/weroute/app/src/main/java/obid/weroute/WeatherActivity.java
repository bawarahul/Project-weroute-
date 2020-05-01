package obid.weroute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    String latitude,longitude;
    ImageView day,fg,night;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
             Intent intent=getIntent();
              Calendar c=Calendar.getInstance();
        int timeOfDay=c.get(Calendar.HOUR_OF_DAY);
              longitude=intent.getExtras().getString("longitude");
              latitude=intent.getExtras().getString("latitude");
            cityField = (TextView)findViewById(R.id.city_field);
            day=(ImageView)findViewById(R.id.day);
            night=(ImageView)findViewById(R.id.night);
            fg=(ImageView)findViewById(R.id.weatherfg);
            updatedField = (TextView)findViewById(R.id.updated_field);
            detailsField = (TextView)findViewById(R.id.details_field);
            currentTemperatureField = (TextView)findViewById(R.id.current_temperature);
            humidity_field = (TextView)findViewById(R.id.humidity_field);
            pressure_field = (TextView)findViewById(R.id.pressure_field);
            weatherIcon = (TextView)findViewById(R.id.weather_icon);
            weatherIcon.setText(latitude+" "+longitude);
            if(timeOfDay>=4 &&timeOfDay<18){
                day.setVisibility(View.VISIBLE);
                night.setVisibility(View.INVISIBLE);
            }
            else {
                day.setVisibility(View.INVISIBLE);
                night.setVisibility(View.VISIBLE);
            }

            Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
                public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                    cityField.setText(weather_city);
                    updatedField.setText(weather_updatedOn);
                    detailsField.setText(weather_description);
                    currentTemperatureField.setText(weather_temperature);
                    humidity_field.setText("Humidity: "+weather_humidity);
                    pressure_field.setText("Pressure: "+weather_pressure);
                    if(weather_iconText.equals("&#xf00d;")||weather_iconText.equals("&#xf02e;")){
                        fg.setImageResource(R.drawable.clear);
                    }
                    else if(weather_iconText.equals("&#xf01c;")||weather_iconText.equals("&#xf019;")){

                        fg.setImageResource(R.drawable.rainy);
                    }
                    else if(weather_iconText.equals("&#xf013;")){

                        fg.setImageResource(R.drawable.cloudy);
                    }
                    else if(weather_iconText.equals("&#xf01b;")){

                        fg.setImageResource(R.drawable.fog);
                    } else if(weather_iconText.equals("&#xf01e;")){

                        fg.setImageResource(R.drawable.thunder);
                    } else if(weather_iconText.equals("&#xf014;")){

                        fg.setImageResource(R.drawable.fog);
                    }

                }
            });
            asyncTask.execute(latitude, longitude); //  asyncTask.execute("Latitude", "Longitude")



        }

    }

