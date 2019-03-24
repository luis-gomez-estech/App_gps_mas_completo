package com.luisgomez.gps_envio_sms_y_mapas;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPS extends Activity {

    private TextView Altura, Precision;
    private Location loc;

    TextView mensaje1;
    TextView mensaje2;
    TextView mensaje3;

    private Button verMapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        mensaje1 = (TextView) findViewById(R.id.mensaje_id);
        mensaje2 = (TextView) findViewById(R.id.mensaje_id2);
        mensaje3 = (TextView) findViewById(R.id.mensaje_id3);

        Altura = (TextView)findViewById(R.id.Altitud);
        Precision = (TextView)findViewById(R.id.Precision);

        verMapa = (Button) findViewById(R.id.btnVerMapa);

        verMapa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(GPS.this, MapasActivity.class);
                startActivity(i);
                finish();
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }



    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPS.Localizacion Local = new GPS.Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

        mensaje1.setText("Localizacion agregada");
        mensaje2.setText("");
        mensaje3.setText("");

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    mensaje3.setText(DirCalle.getAddressLine(0));
                    Altura.setText(String.valueOf(loc.getAltitude()));
                    Precision.setText(String.valueOf(loc.getAccuracy()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        GPS gps;

        public GPS getMainActivity() {
            return gps;
        }

        public void setMainActivity(GPS gps) {
            this.gps = gps;
        }

        @Override
        public void onLocationChanged(Location loc) {

            // Este metodo se va a ejecutar cada vez que el GPS reciba nuevas coordenadas
            // cuando se detecte un cambio de ubicacion 8cuando nos movamos hacia otro lado)

            loc.getLatitude();
            loc.getLongitude();

            final String LatitudString = String.valueOf(loc.getLatitude());
            final String LongitudString = String.valueOf(loc.getLongitude());

            mensaje1.setText(LatitudString);
            mensaje2.setText(LongitudString);
            this.gps.setLocation(loc);

            // Aqui el boton y demás codigo para enviar un sms a uno o varios nuemros de telefono a la vez

            final Button button = findViewById(R.id.btnEnviar);

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        // TextView co = (TextView) findViewById(R.id.mensaje_id); // Lo hago de otra forma
                        //String messageToSend = co.getText().toString();

                        TextView di = (TextView) findViewById(R.id.mensaje_id3);
                        String messageToSend2 = di.getText().toString();

                        if (ContextCompat.checkSelfPermission(GPS.this,
                                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(GPS.this,
                                    Manifest.permission.SEND_SMS)) {
                                ActivityCompat.requestPermissions(GPS.this,
                                        new String[]{Manifest.permission.SEND_SMS}, 1);
                            } else {
                                ActivityCompat.requestPermissions(GPS.this,
                                        new String[]{Manifest.permission.SEND_SMS}, 1);
                            }
                        }else {

                            //Aqui se añaden los tlf a los que queremos enviar la posicion

                            //TELEFONO 1

                            String number = "Aqui sustituimos todo este texto por el numero de telefono donde queremos enviar el sms y con el formato 666666666";

                            SmsManager.getDefault().sendTextMessage(number, null, "Mi posicion es: " + "\n"
                                    + "Latitud: " + LatitudString + "\n"
                                    + "Longitud: " + LongitudString + "\n" + "\n"
                                    + "Estoy en: " + "\n" + messageToSend2 , null, null);

                            /* Podemos seguir añadiendo más telefonos para enviar a varios numeros simultaneamente */

                            /*
                            //TELEFONO 2

                            String number2 = "666666666";

                            SmsManager.getDefault().sendTextMessage(number2, null, messageToSend2 + "\n" +
                                    messageToSend, null, null);
                            */


                        }

                        Toast.makeText(getApplicationContext(), "Mensaje Enviado!",
                                Toast.LENGTH_LONG).show();

                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), "Fallo el envio!",
                                Toast.LENGTH_LONG).show();

                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
}



