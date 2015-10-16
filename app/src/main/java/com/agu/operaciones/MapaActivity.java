package com.agu.operaciones;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.ConfiguracionGPS;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

;

public class MapaActivity extends FragmentActivity implements LocationSource,
        LocationListener, View.OnClickListener {

    //private LocationManager locManager;
    private OnLocationChangedListener locationListener = null;

    private Location objetivoLocation;

    private GoogleMap mapa;
    private LatLng localidad;

    private Double lat;
    private Double lon;
    private Double distancia;
    private Double rango;
    private String numTicket;
    private Boolean sincronizado = true;

    private String TAG = "MapaActivity";

    private ConfiguracionGPS cGPS;


    Button siguienteBtn, atrasBtn, objetivoBtn;
    Context contexto;
    Bundle b;


    private int tipoMapa=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Mapa");
        setContentView(R.layout.activity_mapa);
        contexto = this;
        //Objeto que gestiona la configuracion de gps, se envia el contexto y una condicional que evalua si proviene de mapa
        cGPS = new ConfiguracionGPS(contexto,true);
        mapa = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        mapa.setMyLocationEnabled(true);
        rango= 100.0;
        b = this.getIntent().getExtras();

        String siSincroniza=b.getString(TicketTable.KEY_Sincronizado);
        Log.i(TAG, "que trae siSincroniza " + siSincroniza);
        siguienteBtn = (Button) findViewById(R.id.siguiente_boton);

		/*
		 * SOLO EN MODO DEBUG , SE ANULA LA VALIDACIoN DE CERCANIA A UN DETERMINADO RADIO DE
		 * LA LOCALIZACION DEL TICKET PARA SER ACTIVADO EL BOTON SIGUIENTE
		 * En modo debug, siempre esta activo el boton siguiente
		 */
        if(SplashActivity.MODO_DEBUG)
            siguienteBtn.setVisibility(Button.VISIBLE);

        if (siSincroniza.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)) {
            sincronizado = true;
        }else{
            sincronizado = false;
            siguienteBtn.setVisibility(Button.VISIBLE);
        }

        //Oficina
        //lat = 19.367571;
        //lon = -99.161933;
        numTicket = b.getString(TicketTable.KEY_NumTicket);
        //Plaza universidad
        //lat =19.367571;
        //lon = -99.16615;
        //BD
        if(b.getBoolean("tipoEtapa")){
            lat = b.getDouble(TicketTable.KEY_Latitud);
            lon = b.getDouble(TicketTable.KEY_Longitud);
        }else{
            lat = b.getDouble(TicketTable.KEY_LatitudInicial);
            lon = b.getDouble(TicketTable.KEY_LongitudInicial);
        }
        Log.i("que trae la lat y long", "lat: " + lat + " " + "long:" + lon);

        objetivoLocation= new Location("a");
        objetivoLocation.setLatitude(lat);
        objetivoLocation.setLongitude(lon);
        objetivoUbicacionMapa(lat, lon);

        //se comenta para que quede en productivo
        //siguienteBtn.setVisibility(Button.VISIBLE);
        siguienteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG,b.getString(TicketTable.KEY_Etapa));
//                if(b.getString(TicketTable.KEY_Etapa).equals(TicketMetaData.CONST_ETAPA_SUPERVISION)){
                    Intent i = new Intent(contexto, Detalle3Reporte.class);
                    i.putExtras(b);
                    startActivity(i);
//                }else{
//                    Intent i = new Intent(contexto, ConclusionDetalle4Reporte.class);
//                    i.putExtras(b);
//                    startActivity(i);
//                }

            }
        });
        atrasBtn = (Button)findViewById(R.id.backbutton_map);
        atrasBtn.setOnClickListener(this);

        objetivoBtn = (Button) findViewById(R.id.objetivo_boton);
        objetivoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                localidad = new LatLng(lat, lon);
                CameraPosition camPos = new CameraPosition.Builder().target(localidad)
                        .zoom(17).build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

                mapa.animateCamera(camUpd3);

            }
        });

    }

    private void objetivoUbicacionMapa(double latitud, double longitud) {
        localidad = new LatLng(latitud, longitud);
        CameraPosition camPos = new CameraPosition.Builder().target(localidad)
                .zoom(17).build();

        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

        mapa.animateCamera(camUpd3);
        mapa.addMarker(new MarkerOptions()
                .position(new LatLng(latitud, longitud)).title(b.getString(TicketMetaData.TicketTable.KEY_Tramo))
                .snippet("Lat: "+latitud+" Lon: "+longitud));

        mapa.addCircle(new CircleOptions().center(localidad).radius(rango)
                .strokeWidth(6).strokeColor(0xFF0000FF).fillColor(0x110000FF));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tipo_mapa:
                if(tipoMapa==1){
                    mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    item.setTitle(R.string.mapa_normal);
                    tipoMapa=2;
                }else{
                    mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    item.setTitle(R.string.mapa_satelital);
                    tipoMapa=1;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locationListener != null) {
            locationListener.onLocationChanged(location);

            double lat2 = location.getLatitude();
            double lon2 = location.getLongitude();

            distancia = (double) objetivoLocation.distanceTo(location);
            if(distancia<rango){
                siguienteBtn.setVisibility(Button.VISIBLE);
                distancia = Math.round( distancia * 100.0 ) / 100.0;
                CharSequence text = "Estoy a "+distancia+"mts. del objetivo";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(this, text, duration).show();
            }else{
                //Diferenciamos el modo debug
                if(SplashActivity.MODO_DEBUG)
                {
                    siguienteBtn.setVisibility(Button.VISIBLE);
                }else
                {
                    //En modo Productivo
                    if(sincronizado){
                        siguienteBtn.setVisibility(Button.GONE);
                    }
                }



            }


            Log.d("geolocalizacion es", "lat2: " + lat2 + "\n" + "lon2: " + lon2);
            Log.d("laa distancia recorrida", Double.toString(distancia));
        } else {
            Log.d("geolocalizacion es", "no hay geolocalizacion");
        }

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        locationListener = listener;
        //Log.d("activate", "si");

    }

    @Override
    public void deactivate() {
        locationListener = null;
        //Log.d("activate", "no");
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onResume(){
        super.onResume();
        cGPS.configuracionLocationManager();
        mapa.setLocationSource(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapa.setLocationSource(null);
        cGPS.removeUpdatesMapa().removeUpdates(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        super.onBackPressed();

    }
}
