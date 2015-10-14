package com.agu.operaciones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.utilities.SessionManager;
import com.agu.operaciones.webservices.DataWebservice;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsuario, txtPassword;
    private TextView softwareVersionTextV;

    private Button btnLogin;

    public static String USUARIO_SUP = "";

    SessionManager session;

    private HashMap<String, Boolean> validaciones;



    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        validaciones = new HashMap<String, Boolean>();
        validaciones.put("usuario", false);
        validaciones.put("password", false);

        session = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_login);
        initUI();

    }

    private void initUI() {
        txtUsuario = (EditText) findViewById(R.id.editTextUser);
        txtPassword = (EditText) findViewById(R.id.editTextPass);

        txtUsuario.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(s.toString().trim().length() == 0){
                    validaciones.put("usuario", false);
                }else{
                    validaciones.put("usuario", true);
                }
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(s.toString().trim().length() == 0){
                    validaciones.put("password", false);
                }else{
                    validaciones.put("password", true);
                }
            }
        });

        txtUsuario.setText(session.ultimoUsuarioIngresado());
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Obtengo el user y passs del edittext
                String usuario = txtUsuario.getText().toString().trim();
                String pass = txtPassword.getText().toString();

                //recordamos el ultimo nombre de usuario ingresado
                session.recordarUsuario(usuario);

                // Verificacio de usuario y pass estan llenados
                if (validaciones.containsValue(false)) {

                    // username / password doesn't match
                    Toast.makeText(getApplicationContext(),
                            "El campo usuario y/o password vació",
                            Toast.LENGTH_SHORT).show();

                } else {
                    new Autentica(LoginActivity.this, usuario, pass).execute("");

                }
            }
        });

        //Ponemos la versión actual del software
        softwareVersionTextV = (TextView)findViewById(R.id.softwareVersionTextV);
        softwareVersionTextV.setText(SplashActivity.SOFTWARE_VERSION);
    }



    private class Autentica extends AsyncTask<String, Integer, Integer> {
        // Respuestas al proceso de doInBackground
        protected static final int RESPUESTA_EXITOSA = 0;
        protected static final int RESPUESTA_ERROR = 1;
        private int respuesta=RESPUESTA_ERROR;
        Activity contexto;
        ProgressDialog pd = null;
        String usuario, contrasena, idSup, nombre,idTelefono,nomSup,horaInicio,cuadrilla;

        private String mensajeError = "";

        public Autentica(Activity ctx, String usuario, String contrasena) {

            contexto = ctx;
            this.usuario = usuario;
            this.contrasena = contrasena;
        }

        @Override
        protected void onPreExecute() {
            // Corre en el UI thread
            super.onPreExecute();
            pd = ProgressDialog.show(contexto, "Aviso", "Accediendo...", true);
        }

        /*
         * En este proceso del
         */
        @Override
        protected Integer doInBackground(String... args) {


            // Objeto de parametros de envio al servicio de autenticación
            List<NameValuePair> logMe = new ArrayList<NameValuePair>();
            logMe.add(new BasicNameValuePair("UsuarioSup", usuario));
            logMe.add(new BasicNameValuePair("Password", contrasena));

            try {
                JSONObject usuarioData = DataWebservice.callService(
                        DataWebservice.SUPERVISOR, logMe,
                        DataWebservice.METHOD_POST,
                        contexto);


                String valor = usuarioData.getString("Error");
                if (valor.equals("false")) {
                    idSup = usuarioData.getString(SessionManager.IDSUP);
                    nombre = usuarioData.getString(SessionManager.NOMBRE);
                    idTelefono = usuarioData.getString(SessionManager.IDTEL);
                    horaInicio = usuarioData.getString(SessionManager.HORAINICIO);
                    cuadrilla = usuarioData.getString(SessionManager.CUADRILLA);
                    respuesta = RESPUESTA_EXITOSA;
                    mensajeError = usuarioData.getString("ErrorMsg");
                } else {
                    mensajeError = usuarioData.getString("ErrorMsg");
                    respuesta = RESPUESTA_ERROR;

                }
            } catch (JSONException e) {

                respuesta = RESPUESTA_ERROR;
                mensajeError = e.getMessage();

            }catch(NoInternetException nie)
            {
                respuesta = RESPUESTA_ERROR;
                mensajeError = nie.getMessage();
            }


            return respuesta;

        }

        protected void onPostExecute(final Integer respuesta) {
            // desecha el dialogo antes de Terminar de cargar los productos
            pd.cancel();
            if(respuesta.intValue() == RESPUESTA_EXITOSA){

                AlertDialog aD = new AlertDialog.Builder(LoginActivity.this).create();
                aD.setTitle("Atención");

                // Setting Dialog Message
                aD.setMessage(mensajeError);
                aD.setCancelable(false);
                aD.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        session.iniciarSession(usuario, nombre, idSup,idTelefono,cuadrilla,horaInicio);
                        final Intent i = new Intent(LoginActivity.this, TicketListActivity.class);

                        final Bundle extras = new Bundle();
                        //para que descargue al inicio de TicketActivity
                        extras.putBoolean(TicketMetaData.DESCARGA_TICKETS_INICIO, true);

                        i.putExtras(extras);

                        startActivity(i);
                        ((Activity)contexto).finish();

                    }
                });
                aD.show();
            }else{
                Toast.makeText(contexto, mensajeError, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
