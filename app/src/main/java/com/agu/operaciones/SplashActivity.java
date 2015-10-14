package com.agu.operaciones;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.utilities.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DURATION = 12000;

    public static final boolean MODO_DEBUG = true;

    SessionManager session;
    public static final String SOFTWARE_VERSION;

    static{
        if(MODO_DEBUG)
            SOFTWARE_VERSION = "0.0.1_d";
        else
            SOFTWARE_VERSION = "0.0.1_P";

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        final Activity splashActivity = this;

        Thread splash = new Thread(){
            public void run(){
                try{
                    int timer = 0;
                    while(timer < SPLASH_DURATION){
                        sleep(10);
                        timer += 100;
                    }

                    session = new SessionManager(splashActivity);
                    Intent i;
                    Log.d("Autenticado?: ", "" + session.getAutenticadoStatus());
                    if(session.getAutenticadoStatus()){
                        i = new Intent(getApplicationContext(), TicketListActivity.class);

                        Bundle extras = new Bundle();
                        //para que no descargue al inicio de TicketActivity
                        extras.putBoolean(TicketMetaData.DESCARGA_TICKETS_INICIO, false);
                        i.putExtras(extras);
                    }else{
                        //Accede a la pantalla de login
                        i = new Intent(getApplicationContext(), LoginActivity.class);
                    }
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    finish();
                }
            }
        };
        splash.start();
    }
}
