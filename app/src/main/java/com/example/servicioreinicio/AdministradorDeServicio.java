package com.example.servicioreinicio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AdministradorDeServicio {

    //region Atributos
    private static final String TAG = "AdministradorDeServicio";
    private static Intent intentServicio = null;
    //endregion

    //region Constructor
    public AdministradorDeServicio(){}
    //endregion

    //region Propios
    public void iniciarServicio(Context context){
        if (context == null) {
            return;
        }
        setIntentServicioGPS(context);

        //Se valida las versiones de android al cual va dirigido el apk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentServicio);
        } else {
            context.startService(intentServicio);
        }
        Log.d(TAG, "iniciarServicio:  Service es iniciado....");
    }

    private void setIntentServicioGPS(Context context) {
        if (intentServicio == null) {
            intentServicio = new Intent(context, ServicioGPS.class);
        }
    }
    //endregion
}
