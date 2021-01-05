package com.example.servicioreinicio;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class BroadcastReceiverDeReinicio extends BroadcastReceiver {
    //region Atributos
    private static final String TAG = "BroadcastReceiverDeReinicio";
    private static JobScheduler jobScheduler;
    private BroadcastReceiverDeReinicio broadcastReceiverDeReinicio;
    //endregion

    //region Sobrecargas
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BroadcastReceiverDeReinicio.class.getSimpleName(), "Service se detuvo pero, se reiniciara....");
        programarTrabajo(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void programarTrabajo(Context context) {
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                ServicioTrabajo.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setOverrideDeadline(0)
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);
    }
    //endregion
}
