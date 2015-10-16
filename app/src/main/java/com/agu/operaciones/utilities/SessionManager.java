package com.agu.operaciones.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.agu.operaciones.LoginActivity;

import java.util.HashMap;

/**
 * Created by Cloudco on 14/10/15.
 */
public class SessionManager {

    Context _context;
    SharedPreferences shareP;
    SharedPreferences.Editor editor;

    //archivo de Preferencias
    private final String PREFERENCES_FILE = "SessionPreferences";
    //llave con la que se referencia si esta autenticado el usuario
    private final String ESTADO_SESSION = "autenticado";
    public static final String USUARIO = "UsuarioOp";
    public static final String NOMBRE = "NomSup";
    public static final String IDSUP = "IdSup";
    public static final String IDTEL = "IdTelefono";
    public static final String CUADRILLA = "Cuadrilla";
    public static final String HORAINICIO = "HoraInicio";
    public static final String DEBUG_SESSION = "modo_debug";


    public SessionManager(Context context)
    {
        this._context = context;
        shareP = _context.getSharedPreferences(PREFERENCES_FILE, _context.MODE_PRIVATE);
        editor = shareP.edit();

    }



    public void iniciarSession(String usuario, String nombre, String idSupervisor,String idTel,String cuadrilla,String horaInicio){


        editor.putBoolean(ESTADO_SESSION, true);
        editor.putString(USUARIO, usuario);
        editor.putString(NOMBRE, nombre);
        editor.putString(IDSUP, idSupervisor);
        editor.putString(IDTEL,idTel);
        editor.putString(CUADRILLA, cuadrilla);
        editor.putString(HORAINICIO, horaInicio);
        editor.commit();


    }



    public void cerrarSesion()
    {
        String lastUsername = shareP.getString(USUARIO, "");

        editor.clear();
        //solo preservamos al usuario
        editor.putString(USUARIO, lastUsername);
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        //Cerrando todas las activities previas
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);


    }

    public boolean getAutenticadoStatus(){

        return shareP.getBoolean(ESTADO_SESSION, false);

    }


    public HashMap<String, String> getDetallesSession()
    {
        HashMap< String, String> datosSesion = new HashMap<String, String>();
        datosSesion.put(NOMBRE, shareP.getString(NOMBRE, null));
        datosSesion.put(USUARIO, shareP.getString(USUARIO, null));
        datosSesion.put(IDSUP, shareP.getString(IDSUP, null));
        datosSesion.put(IDTEL, shareP.getString(IDTEL,null));
        datosSesion.put(CUADRILLA, shareP.getString(CUADRILLA,null));
        datosSesion.put(HORAINICIO, shareP.getString(HORAINICIO,null));

        return datosSesion;
    }

    public void recordarUsuario(String usuario){
        editor.putString(USUARIO, usuario);
        editor.commit();
    }

    public String ultimoUsuarioIngresado(){
        return shareP.getString(USUARIO, "");
    }

    public boolean getDebugSessionStatus()
    {
        return shareP.getBoolean(DEBUG_SESSION, false);
    }

    public void setDebugSessionStatus(boolean status)
    {
        editor.putBoolean(DEBUG_SESSION, status);
        editor.commit();
    }

}
