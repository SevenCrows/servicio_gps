package com.example.utilitario.gps;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.utilitario.R;
import com.example.utilitario.gps.trasversal.ConstantesAccion;
import com.example.utilitario.gps.trasversal.ConstantesExtras;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServicioGPS extends Service implements LocationListener {

    //region Atributos
    private Location locacion_actual, locacion_previa;
    //endregion

    //region Constructor
    public ServicioGPS(Context context) {
        Log.i(ServicioGPS.class.getSimpleName(), " --> Servicio en Constructor");
    }

    public ServicioGPS() {
    }
    //endregion

    //region Sobrecarga
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        // Create the Foreground Service
        //https://stackoverflow.com/questions/6397754/android-implementing-startforeground-for-a-service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_f)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(101, notification);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        tareaProgramada();
        Toast.makeText(this, "Esperando conexión con el GPS, por favor espere…", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(ServicioGPS.class.getSimpleName(), " --> Servicio se Destruye");
        Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
    //endregion

    //region Propios
    private void tareaProgramada() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                actualizarLocalizacionPrevia();
                handler.postDelayed(this, 15000); //Cada segundo.
            }
        };
        handler.postDelayed(r, 15000);
    }

    private void crearArchivoVelocidad(Location location) {
        try {
            //Read text from file
            StringBuilder text = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            String date = simpleDateFormat.format(new Date());
            //Nombre del archivo
            String nombreArchivo = "registro_velocidades.txt";

            String contenido = String.format("H:%s, Lat:%s, Lon:%s, Vel:%s", date, location.getLatitude(), location.getLongitude(), location.getSpeed() * 3.6f);
            ;
            //Creacion de carpeta SpeedoMeter
            File myDirectory = new File(this.getExternalFilesDir(null), "Speedometer/");
            if (!myDirectory.exists()) {
                boolean creacionDirectorio = myDirectory.mkdirs();
            }
            //verificar que exista elarchivo para leer
            File archivo = new File(myDirectory, nombreArchivo);
            if (archivo.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append("\n");
                }
                br.close();
            }
            text.append(contenido);
            //Creacion del archivo en el dispositivo
            FileOutputStream fout = new FileOutputStream(new File(myDirectory, nombreArchivo));
            fout.write(text.toString().getBytes());
            fout.close();
        } catch (IOException e) {
            Toast.makeText(this.getApplication(), "Error al modificar archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarLocalizacionPrevia() {
        if (locacion_actual != null && locacion_previa != null) {
            if (locacion_previa.distanceTo(locacion_actual) >= 50) {
                locacion_previa = locacion_actual;
                crearArchivoVelocidad(locacion_actual);
            }
        }
    }
    //endregion

    //region Contrato
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(ServicioGPS.class.getSimpleName(), " L --> " + location.toString());
        float velocidadKmH = (location.getSpeed()) * 3.6f;

        if (locacion_previa == null) {
            locacion_previa = location;
            crearArchivoVelocidad(location);
        }

        actualizarLocalizacionPrevia();

        locacion_actual = location;
        if (velocidadKmH >= 50) crearArchivoVelocidad(location);

        Intent intent = new Intent(ConstantesAccion.ACCION_INTENT);
        intent.putExtra(ConstantesExtras.EXTRA_LOCALIZACION, String.valueOf(velocidadKmH));
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(intent);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    //endregion
}

