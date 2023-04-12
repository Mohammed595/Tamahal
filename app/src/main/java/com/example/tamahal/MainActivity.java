package com.example.tamahal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView titleNotfy;
    TextView descriptionNotfy;

    CardView cardMake  ;
    CardView cardAsked ;
    Retrofit retrofit;

    LocationManager locationManager;
    LocationListener locationListener;


    int arrTime[];
    static int count = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView
        titleNotfy = findViewById(R.id.titleNotfy);
        descriptionNotfy = findViewById(R.id.discriptionNotfy);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://tamahall.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        cardAsked = findViewById(R.id.cardAsked);
        cardMake = findViewById(R.id.cardMake);

//        cardAsked.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent( MainActivity.this , AskedHelp.class ) ;
//                startActivity(intent);
//            }
//        });



        count = 0;
        arrTime = new int[100];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                        getFromCam();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();


    }


    void getFromCam() {


        Api apiInterface = retrofit.create(Api.class);

        Call<Post> call = apiInterface.getPost();

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        getLocation(location, response, 1);
                    }
                    //
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        LocationListener.super.onStatusChanged(provider, status, extras);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        LocationListener.super.onProviderDisabled(provider);
                    }
                };
                turnOnLocation();


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

    }


    void turnOnLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }


    boolean searchTime(int[] arr, int time) {

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == time) {
                return false;
            }
        }

        return true;
    }


    public void notifyMethod(String title, String description) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("id", "channelName", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("channelDesc");
            NotificationManager mn1 = getSystemService(NotificationManager.class);
            mn1.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id");
        builder.setAutoCancel(true);
        builder.setNumber(1);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setSmallIcon(R.drawable.warning);

        NotificationManagerCompat mn = NotificationManagerCompat.from(this);

        mn.notify(1, builder.build());
    }


    // get Location
    public void getLocation(Location location, Response<Post> response, int who) {

        double longNum = location.getLongitude();
        double latNum = location.getLatitude();



        float[] results = new float[1];

        if (who == 1) {
            // un Location
            // 27.6656, 41.7222
            Location.distanceBetween(latNum, longNum , latNum, longNum, results);
        } else if (who == 2){
            // cam Location
            Location.distanceBetween(27.5454, 41.7570, latNum, longNum, results);
        }else if (who == 3) {
            // ga
            // 27.6656, 41.7222
            Location.distanceBetween(27.6656, 41.7222, latNum, longNum, results);
        }
        float distanceInMeters = results[0];
        boolean isWithin = distanceInMeters < 3000;

        int varTime = response.body().getCounter();

        if (isWithin == false) {
            titleNotfy.setText("");
            descriptionNotfy.setText("");
        }

        if (searchTime(arrTime, varTime) && isWithin) {

            // Notify Method
            notifyMethod(response.body().getTitle(), response.body().getDescription());
            titleNotfy.setText(response.body().getTitle());
            descriptionNotfy.setText(response.body().getDescription());
            arrTime[count] = varTime;
            ++count;

        }

    }
}