package com.example.servicioreinicio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.utilitario.gps.RestartBroadcastReceiver;
import com.example.utilitario.gps.trasversal.ConstantesAccion;
import com.example.utilitario.gps.trasversal.CodigosPermiso;
import com.example.utilitario.gps.trasversal.ConstantesExtras;

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
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantesAccion.ACCION_INTENT);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iniciarPrograma();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CodigosPermiso.PERMISOS_APLICACION) {
            boolean permisosGarantizados = true;

            for (int validacion : grantResults)
                if (validacion != PackageManager.PERMISSION_GRANTED) permisosGarantizados = false;

            if (permisosGarantizados) {
                iniciarPrograma();
            } else {
                this.finish();
            }
        }
    }
    //endregion

    //region Propios
    private void iniciarPrograma() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, getResources().getStringArray(R.array.permisos_aplicacion), CodigosPermiso.PERMISOS_APLICACION);
        } else {
            //iniciar Programa
            RestartBroadcastReceiver.programarTrabajo(getApplicationContext());

        }
    }
    //endregion

    //region Listener
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantesAccion.ACCION_INTENT)) {
                final String param = intent.getStringExtra(ConstantesExtras.EXTRA_LOCALIZACION);

                tv_kilometraje.setText(String.format("%s km/h", param));
            }
        }
    };
    //endregion
}