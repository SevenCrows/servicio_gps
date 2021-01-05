package com.example.utilitario.gps;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.utilitario.gps.trasversal.ConstantesIntent;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ServicioTrabajo extends JobService {

    //region Atributos
    private static RestartBroadcastReceiver restartBroadcastReceiver;
    //endregion

    //region Sobrecarga
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(ServicioTrabajo.class.getSimpleName(), " --> Inicia tarea.....");
        AdminServicio admin = new AdminServicio();
        admin.iniciarServicio(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(ServicioTrabajo.class.getSimpleName(), " --> Se detiene la tarea.....");
        Intent broadcastIntent = new Intent(ConstantesIntent.INTENT_REINICIAR);
        sendBroadcast(broadcastIntent);

        new Handler().postDelayed(() -> unregisterReceiver(restartBroadcastReceiver), 1000);
        return false;
    }
    //endregion
}