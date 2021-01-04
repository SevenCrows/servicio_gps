package com.example.servicioreinicio;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.servicioreinicio.trasversal.ConstanteIntent;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ServicioTrabajo extends JobService {

    //region Atributos
    private static final String TAG = "ServicioTrabajo";
    private static BroadcastReceiverDeReinicio broadcastReceiverDeReinicio;
    private static ServicioTrabajo instancia;
    private static JobParameters parametrosTrabajo;
    //endregion

    //region Sobrecarga
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, " --> Inicia la tarea");
        AdministradorDeServicio admin = new AdministradorDeServicio();
        admin.iniciarServicio(this);
        instancia = this;
        parametrosTrabajo = params;

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, " --> Para la tarea");
        Intent broadcastIntent = new Intent(ConstanteIntent.REINICIAR_INTENT);
        sendBroadcast(broadcastIntent);

        new Handler().postDelayed(() -> unregisterReceiver(broadcastReceiverDeReinicio), 1000);

        return false;
    }
    //enderegion
}
