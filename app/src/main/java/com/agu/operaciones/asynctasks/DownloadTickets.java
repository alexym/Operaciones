package com.agu.operaciones.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.utilities.SessionManager;
import com.agu.operaciones.webservices.DataWebservice;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Cloudco on 14/10/15.
 */
public class DownloadTickets extends AsyncTask<String, Integer, Integer> {

    // Respuestas al proceso de doInBackground
    String TAG = "DownloadTickets";
    private final int RESPUESTA_EXITOSA = 0;
    private final int RESPUESTA_ERROR = 1;
    //private TicketsAdapter dbAdapter;

    Activity contexto;
    ProgressDialog pd = null;
    private String mensajeError = "";

    public DownloadTickets(Activity ctx, String tagged) {
        contexto = ctx;
        pd = null;

    }

    @Override
    protected void onPreExecute() {
        // Corre en el UI thread
        super.onPreExecute();
        pd = ProgressDialog.show(contexto, "Aviso",
                "Descargando Tickets...", true);
    }

		/*
		 * M�todo que nos permite invocar al servicio para validar que todos los
		 * tickets hayan sido recibidos correctamente.
		 *
		 * @return int - valor correspondiente al resultado de validaci�n. Puede
		 * ser alguno de estos valores: RESPUESTA_EXITOSA (0) RESPUESTA_ERROR
		 * (1)
		 */

    private void validaTicketsRecibidos()
            throws SQLException,
            NoInternetException, JSONException {
        ContentResolver cresolver = contexto.getContentResolver();
			/*
			 * OPERACION DE VALIDACION DE TICKETS DESCARGADOS
			 */
        String[] projection = {TicketTable.KEY_ROWID, TicketTable.KEY_IdTicket,
                TicketTable.KEY_NumTicket, TicketTable.KEY_Etapa,
                TicketTable.KEY_Sincronizado};

        Cursor ticketsCursor = cresolver.query(TicketTable.CONTENT_URI,projection,null,null,null);


        // Obtenemos el indice de la propiedad idTicket
        int idTicketIndex = ticketsCursor.getColumnIndex(TicketTable.KEY_IdTicket);

        JSONObject paramEnvio = new JSONObject();
        JSONArray ticketsIDS = new JSONArray();
        SessionManager sm = new SessionManager(contexto);
        HashMap<String, String> hm = sm.getDetallesSession();
        String usuarioSup = hm.get(SessionManager.USUARIO);
        paramEnvio.put("UsuarioSup", usuarioSup);
        if (ticketsCursor.moveToFirst()) {

            do {
                JSONObject tempObj = new JSONObject();
                tempObj.put(TicketTable.KEY_IdTicket,
                        ticketsCursor.getString(idTicketIndex));
                Log.i(TAG + " id cursor", ticketsCursor.getString(idTicketIndex));
                ticketsIDS.put(tempObj);
            } while (ticketsCursor.moveToNext());

            paramEnvio.put("LTickets", ticketsIDS);

        }

        Log.d("Validacion Tickets", "" + paramEnvio);
        JSONObject respuestaValidationT = DataWebservice.callService(DataWebservice.VALIDARSINC, paramEnvio, contexto);

        Log.d(TAG+"Val Tick res", " " + respuestaValidationT.toString());
        if (respuestaValidationT.get("Error").equals("true")) {
            String mensajeError = (String) respuestaValidationT
                    .get("ErrorMsg");
            //throw new TicketsIncompletosException(mensajeError);

        }

    }

    private void insertaTicketsRecibidos(JSONObject ticket){
        ContentValues cv = new ContentValues();
        ContentResolver cresolver = contexto.getContentResolver();
        Uri resultUri;
        Iterator it = ticket.keys();
        while (it.hasNext()) {
            String key = (String) it.next();

            try {

                if (ticket.getString(key) == null) {
                    cv.putNull(key);
                } else {
                    cv.put(key, ticket.getString(key));
                    //Log.i(TAG+" inserta ticket", ticket.getString(key));

                }


            } catch (JSONException e) {
                Log.e(TAG+"->creaTicket()", e.getMessage());
                e.printStackTrace();
            }


        }
        resultUri=cresolver.insert(TicketTable.CONTENT_URI, cv);
        Log.i(TAG,"objeto: "+ticket.toString());
        Log.i(TAG+" uri",resultUri.toString());
        //return db.insert(TABLE_NAME, null, cv);
    }

    /*
     * En este proceso del
     */
    @Override
    protected Integer doInBackground(String... args) {

        //int noSincronizados = dbAdapter.getTotalTicketsNoSincronizados();
        Integer respuesta = RESPUESTA_EXITOSA;

        //if (noSincronizados == 0) {

        List<NameValuePair> logMe = new ArrayList<NameValuePair>();

        SessionManager sm = new SessionManager(contexto);
        HashMap<String, String> hm = sm.getDetallesSession();
        String usuarioSup = hm.get(SessionManager.USUARIO);
        logMe.add(new BasicNameValuePair("UsuarioSup", usuarioSup));

        // *** OPERACION DE CONSULTA AL SERVICIO ***
        try {

            JSONObject ticketsData = DataWebservice.callService(DataWebservice.SINCRONIZAR, logMe,
                    DataWebservice.METHOD_GET, contexto);

            String respuestaError = null;

            respuestaError = ticketsData.getString("Error");

            if (respuestaError.equals("true")) {
                respuesta = RESPUESTA_ERROR;
                mensajeError = ticketsData.getString("ErrorMsg");
            } else {

                // *** OPERACION DE INSERCI�N A LA BASE DE DATOS ***
                JSONArray ticketsArray = ticketsData.getJSONArray("lTickets");
                int total = ticketsArray.length();
                for (int i = 0; i < total; i++) {
                    try {
                        insertaTicketsRecibidos(ticketsArray.getJSONObject(i));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        //e.printStackTrace();
                    }
                }


                // *** OPERACION DE VALIDACION ***
                validaTicketsRecibidos();

            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.e(TAG+" exception Json",
                    ex.getMessage());
            //dbAdapter.eliminarTickets();
            respuesta = RESPUESTA_ERROR;
        }
//				catch (TicketsIncompletosException tie) {
//					mensajeError = tie.getMessage();
//					Log.e("error", tie.getMessage());
//					dbAdapter.eliminarTickets();
//					respuesta = RESPUESTA_ERROR;
//
//				}
        catch (SQLException sqle) {

            Log.e(TAG+"->doInBackground()",
                    "Error al tratar de insertar los tickets");
            respuesta = RESPUESTA_ERROR;
        }
        catch (NoInternetException e) {
            mensajeError = e.getMessage();
            respuesta = RESPUESTA_ERROR;
        }


//			} else {
//
//				respuesta = RESPUESTA_ERROR;
//				mensajeError = "Tickets Aún por sincronizar";
//
//			}


        return respuesta;

    }

    // private void compruebaTickets throws
		/*
		 * Despues de consumir el servicio se cierra el progress dialog
		 */
    protected void onPostExecute(final Integer respuesta) {
        // desecha el dialogo antes de Terminar de cargar los productos
        pd.cancel();

        if (respuesta.intValue() == RESPUESTA_EXITOSA) {

            if (respuesta.intValue() == RESPUESTA_EXITOSA) {
                // Cargamos los datos en la interf�z
                //loaderManager.initLoader(TICKET_LOADER_ID, null,TicketActivity.this);
                contexto.getLoaderManager().initLoader(1, null, (LoaderManager.LoaderCallbacks<Cursor>) contexto);
            } else {

                Toast.makeText(contexto, mensajeError,
                        Toast.LENGTH_LONG).show();

            }


        } else {
            // Toast.makeText(contexto, mensajeError, Toast.LENGTH_SHORT)
            // .show();
            AlertDialog aD = new AlertDialog.Builder(contexto).create();
            aD.setTitle("Ateción");

            // Setting Dialog Message
            aD.setMessage("No es posible sincronizar los Tickets en este momento, comunicate con tu administrativo. ");
            aD.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                }
            });
            aD.show();
        }

    }


}
