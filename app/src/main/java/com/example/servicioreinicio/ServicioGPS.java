package com.example.servicioreinicio;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.servicioreinicio.trasversal.ConstanteExtras;
import com.example.servicioreinicio.trasversal.ConstantesAccion;

public class ServicioGPS extends Service implements LocationListener {
    //region Atributos
    private static final String TAG = "ServicioGPS";
    //endregion

    //region Constructor
    public ServicioGPS(Context context) {
        Log.i(TAG, " --> Servicio en Constructor");
    }
    public ServicioGPS() {}
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " --> Servicio se Destruye");
        Intent broadcastIntent = new Intent(this, BroadcastReceiverDeReinicio.class);
        sendBroadcast(broadcastIntent);
        //TODO: Evento de finalizacion de ser necesario
    }

    //endregion

    //region Contrato

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, " L --> " + location.toString());

        Intent intent = new Intent(ConstantesAccion.ACCION_INTENT);
        intent.putExtra(ConstanteExtras.EXTRA_LOCALIZACION, String.valueOf(location.getLongitude()));
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
