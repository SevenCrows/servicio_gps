package com.example.servicioreinicio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.servicioreinicio.trasversal.ConstanteExtras;
import com.example.servicioreinicio.trasversal.ConstantesAccion;

public class MainActivity extends AppCompatActivity {

    //region atributos
    private TextView tv_kilometraje;
    //endregion

    //region Sobrecargas
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_kilometraje = findViewById(R.id.tv_kilometraje);

        //iniciar Programa;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantesAccion.ACCION_INTENT);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BroadcastReceiverDeReinicio.programarTrabajo(getApplicationContext());
    }
    //endregion

    //region Listener
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantesAccion.ACCION_INTENT)) {
                final String param = intent.getStringExtra(ConstanteExtras.EXTRA_LOCALIZACION);

                tv_kilometraje.setText(param);
            }
        }
    };
    //endregion
}