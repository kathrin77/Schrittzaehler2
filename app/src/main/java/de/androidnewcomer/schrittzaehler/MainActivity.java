package de.androidnewcomer.schrittzaehler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int schritte=0;
    private TextView textView;
    private ErschuetterungsHandler handler = new ErschuetterungsHandler();
    private ErschuetterungListener listener = new ErschuetterungListener(handler);

    public class ErschuetterungsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            schritte++;
            aktualisiereAnzeige();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        textView = (TextView)findViewById(R.id.schritte);
        aktualisiereAnzeige();
        findViewById(R.id.button).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    private void aktualisiereAnzeige() {
        textView.setText(Integer.toString(schritte));
        if (schritte %10 == 0 && schritte > 1) {
            createNotification();
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button) {
            schritte=0;
            aktualisiereAnzeige();
        }
    }

    private void createNotification() {

        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";

        //Notification: Inhalt & Look
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Nachricht vom Schrittzähler")
                        .setContentText("Bravo! Du bist "+schritte+" Schritte gelaufen! Weiter so!")
                        //.setContentTitle(getText(R.string.app_name))
                        //.setContentText(getText(R.string.hello_world))
                        .setVibrate(new long[]{400, 700, 500})
                        // Set the notification to cancel when the user taps on it
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        //original:
        Intent resultIntent = new Intent(this, MainActivity.class);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        int mNotificationId = 1;

        // Gibt Metadaten zum Intent hinzu, hier die NotificationId,
        // um die Notification in der ResultActivity canceln zu koennen
        resultIntent.putExtra("notifyID", mNotificationId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // ActionButton fuer Expanded View
        mBuilder.addAction(R.drawable.ic_launcher_background, "Zurücksetzen", resultPendingIntent);

        mNotificationManager.notify(mNotificationId, mBuilder.build());

    }
}


