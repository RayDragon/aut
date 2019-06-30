package com.example.ray.aut;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    Network nw, nws;
    TextView tv;
    Notification n;
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nw = new Network(this, "192.168.43.139"){

            @Override
            public void OnResponse(String response) {
                onResponse(response);
            }
        };
        nws = new Network(this, "192.168.43.182:8000"){
            @Override
            public void OnResponse(String response) {
                sres(response);
            }
        };

        Button
                bt_fan = (Button)findViewById(R.id.button),
                bt_cooler = (Button)findViewById(R.id.button2);

        tv = (TextView)findViewById(R.id.textview);
        bt_fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nw.get_req("5");
            }
        });

        bt_cooler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nw.get_req("6");
            }
        });
        /*Notification*/

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificationManager.notify(0, n);
        //forever loop to keep checking the server
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Long tsLong = (System.currentTimeMillis()/1000)%(24*60*60);
                        String ts = tsLong.toString();
                        nws.get_req("predict?outcome_name=switchcooler&cols=1&val="+ts);
                        sleep(10000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        cooler=fan=false;


    }
    void sres(String s){
        tv.setText(s);
        if(s == "[1.0]" && !cooler) s="Switch on cooler?";
        else if(cooler)s="Switch off the cooler?";
        else return;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        n  = new Notification.Builder(this)
                .setContentTitle("Abnormal uses")
                .setContentText(s)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background, "Call", pIntent)
                .addAction(R.drawable.ic_launcher_background, "More", pIntent)
                .addAction(R.drawable.ic_launcher_background, "And more", pIntent).build();


        notificationManager.notify(0,n);
    }
    boolean cooler,fan;
    void onResponse(String res){
        tv.setText(res);
        try {
            JSONArray ja = new JSONArray(res);
            cooler = ja.getInt(6)==0;
            fan = ja.getInt(5)==0;
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
