package com.luisgomez.gps_envio_sms_y_mapas;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Ests MainActivity principal lo uso de paso, solo para mostrar una pantalla previa al GPS (un loading ...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Para Abrir una pagina de inicio (loading) y despues del tiempo fijado, ira a la ACTIVITY que le asignemos.

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000);  //Tiempo de espera en ms
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(MainActivity.this,
                            GPS.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        welcomeThread.start();


    }

}



