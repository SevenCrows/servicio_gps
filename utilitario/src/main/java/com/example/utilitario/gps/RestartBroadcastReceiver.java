package com.example.utilitario.gps;

import android.annotation.SuppressLint;
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

public class RestartBroadcastReceiver extends BroadcastReceiver {
    //region Atributos
    private static JobScheduler jobScheduler;
    //endregion

    //region Sobrecargas
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartBroadcastReceiver.class.getSimpleName(), "Service se detuvo pero, se reiniciara....");
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