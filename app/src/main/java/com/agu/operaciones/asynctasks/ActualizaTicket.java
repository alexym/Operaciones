package com.agu.operaciones.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.agu.operaciones.TicketListActivity;
import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.webservices.DataWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

;

/**
 * Created by alexym on 15/10/15.
 */
public class ActualizaTicket extends AsyncTask<JSONObject , String, Integer> {

    // Respuestas al proceso de doInBackground
    protected static final int RESPUESTA_EXITOSA = 0;
    protected static final int RESPUESTA_ERROR = 1;

    private final String TAG = "ACTUALIZA TICKET";
    private Activity activity;
    private ProgressDialog pd;

    private String mensajeError = "";
    private int rowID;
    Uri uriBase;
    ContentResolver cresolver;

    public  ActualizaTicket(Activity act) {
        //Log.i
        this.activity = act;

    }
    @Override
    protected void onPreExecute() {
        // Corre en el UI thread
        super.onPreExecute();
        pd = new ProgressDialog(activity);
        pd.setMessage("Actualizando Ticket ...");
        pd.show();
        //pd = ProgressDialog.show(contexto, "Aviso", "Actualizando Ticket ...", true);
    }
    private ContentValues jsonToCV(JSONObject jsonObject){
        ContentValues cv = new ContentValues();
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            String key = (String) it.next();

            try {

                if (jsonObject.getString(key) == null) {
                    cv.putNull(key);
                } else {
                    cv.put(key, jsonObject.getString(key));
                    //Log.i(TAG+" inserta ticket", ticket.getString(key));

                }


            } catch (JSONException e) {
                Log.e(TAG, "->creaTicket()" + e.getMessage());
                e.printStackTrace();
            }


        }
        return cv;
    }

    private int enviaTicket(Cursor ticketCursor, JSONObject params) throws JSONException, NoInternetException {
				/*
	    		 * PROCESO DE SUBIDA DEL TICKET AL WEBSERVICE
	    		 *
	    		 * Seg�n el tipo de ticket (CONCLUSION O SUPERVISION) LOS datos correspondientes a las direcciones y areas
	    		 * ser�n consumidos a trav�s de un servicio o vendr�n directamente en el ticket
	    		 */

        int resp = RESPUESTA_EXITOSA;
        JSONObject reporte = new JSONObject();
        JSONObject completo = new JSONObject();
        JSONArray lista = new JSONArray();
        String etapa = ticketCursor.getString(ticketCursor.getColumnIndex(TicketMetaData.TicketTable.KEY_Etapa));
// Todo editar los parametros a enviar en el ticket
//        if(etapa.equals(TicketMetaData.CONST_ETAPA_SUPERVISION))
//        {
//            reporte.put("Procede",ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Procede)));
//            Log.d("Prioridad seleccionada:", " " + ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_PrioridadSI)));
//            reporte.put("PrioridadSI", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_PrioridadSI)));
//            reporte.put(TicketTable.KEY_Area, params.getString(TicketTable.KEY_Area));
//            reporte.put("DirArea", params.getString(TicketTable.KEY_DirArea));
//            reporte.put("DirGral", params.getString(TicketTable.KEY_DirGral));
//            reporte.put("GpoServicios", params.getString(TicketTable.KEY_GrupoServicios));
//            reporte.put("Servicio", params.getString(TicketTable.KEY_Servicio));
//
//        }else if(etapa.equals(TicketMetaData.CONST_ETAPA_CONCLUSION)){
//
//
//            reporte.put("Procede", "Si");
//            reporte.put("PrioridadSI", "Programado");
//            reporte.put(TicketTable.KEY_Area, ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Area)));
//            reporte.put("DirArea", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_DirArea)));
//            reporte.put("DirGral", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_DirGral)));
//            reporte.put("GpoServicios", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_GrupoServicios)));
//            reporte.put("Servicio", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Servicio)));
//        }else{
//            Log.e(TAG,"ActualizaTicket .> doInBackground ETAPA DESCONOCIDA ");
//        }

        reporte.put(TicketTable.KEY_atendido, ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_atendido)));
        reporte.put("ComentariosSF",ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_ComentariosSF)));
        reporte.put("ComentariosSI", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_ComentarioSI)));
        reporte.put("Etapa", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa)));
        reporte.put("IdTicket", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_IdTicket)));
        reporte.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_SI);
			    /*
			     * HAY QUE ESTANDARIZAR LOS NOMBRES DE LOS CAMPOS  DE LA Imagen
			     */
        String imagenesString = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_uploadImageResponse));
        lista = new JSONArray(imagenesString);

        reporte.put("lImgs", lista);
        completo.put("Tickets", reporte);



        Log.d(TAG,"ACTUALIZANDO TICKET "+ completo.toString());
        JSONObject SMins = DataWebservice.callService(DataWebservice.SMACTTICKET, completo, activity);


        Log.i(TAG,"la respuesta de SMACTTICKET"+SMins.toString());

	            /*
	             * FASE FINAL
	             * ACTUALIZACI�N DEL TICKET A UN ESTADO CONCRETO DE SINCRONIZACI�N DEPENDIENDO DE
	             * LA RESPUESTA DEL WEBSERVICE
	             */
        JSONObject actualizacion = new JSONObject();
        if(SMins.getString("Error").equals(""))
        {
            actualizacion.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_SI);
            ContentValues cv = jsonToCV(actualizacion);
            cresolver.update(uriBase, cv, null, null);


        }else
        {
            //Nos regresa error el webservice
            mensajeError = SMins.getString("ErrorMsg");
            actualizacion.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_NO);
            ContentValues cv = jsonToCV(actualizacion);
            cresolver.update(uriBase, cv, null, null);
            resp = RESPUESTA_ERROR;
        }


        return resp;
    }



    /*
     * Realiza el guardado del ticket en la base de datos y si
     * hay internet, intenta enviar el ticket con el servidor
     */
    @SuppressWarnings("finally")
    @Override
    protected Integer doInBackground(JSONObject ... parametros) {

        int respuesta = RESPUESTA_EXITOSA;
        JSONObject params = parametros[0];
        JSONObject params_id = parametros[1];
        //JSONObject params2 = parametros
        Cursor ticketCursor = null;
        cresolver = activity.getContentResolver();
        try{
            uriBase = ContentUris.withAppendedId(TicketTable.CONTENT_URI, params_id.getInt(TicketTable.KEY_ROWID));
            //ticketCursor = dbAdapter.obtenerTicket(new String[]{"*"}, TicketTable.KEY_NumTicket, params.getString(TicketTable.KEY_NumTicket));
            //String[] projection = {"*"};
            ticketCursor = cresolver.query(
                    uriBase,
                    null,
                    null,//TicketTable.KEY_NumTicket + " = ? " ,
                    null,//new String[] { params.getString(TicketTable.KEY_NumTicket) },
                    null);
            //Log.i(TAG,"muestra el numticket en Actualiza Ticket"+params.getString(TicketTable.KEY_NumTicket));
            if(ticketCursor.moveToFirst()){

                rowID = ticketCursor.getInt((ticketCursor.getColumnIndex(TicketTable.KEY_ROWID)));
                Log.i(TAG,"ACTUALIZA TICKET--> si hay un rowID "+rowID);

                String etapa = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa));

                //comprobamos su estado de sincronizacion
                String estadoSincronizacion = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Sincronizado));
                if(estadoSincronizacion.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)){
		    			/*
		    			 * PROCESO DE ALMACENAMIENTO DEL TICKET DEFINITIVO EN LA BASE
		    			 *
		    			 */

                    params.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_NO);
                    ContentValues cv = jsonToCV(params);
                    cresolver.update(uriBase, cv, null, null);

                    respuesta = enviaTicket(ticketCursor, params);
                }else if(estadoSincronizacion.equals(TicketMetaData.CONST_SINCRONIZADO_NO)){
                    respuesta = enviaTicket(ticketCursor, params);
                }else if(estadoSincronizacion.equals(TicketMetaData.CONST_SINCRONIZADO_SI))
                {
		    			/*
		    			 * el flujo cambio, no valida que se encuentre sincronizado y no lo envia, siempre lo envia.
		    			 */
                    respuesta = RESPUESTA_ERROR;
                    mensajeError = "Ticket ya sincronizado";
                    //respuesta = enviaTicket(ticketCursor, params);
                }

            }

        }catch(JSONException e){

            respuesta = RESPUESTA_ERROR;
            mensajeError = e.getMessage();

        }catch(NoInternetException nie){
            respuesta = RESPUESTA_ERROR;
            mensajeError = nie.getMessage();
        }
//        finally{
//            if(ticketCursor != null)
//                ticketCursor.close();
//            return respuesta;
//        }
        return respuesta;
    }

    private void irInicioFlujo()
    {
        Intent i = new Intent(activity, TicketListActivity.class);
        Bundle extras = new Bundle();
        //para que no descargue al inicio de TicketActivity
        extras.putBoolean(TicketMetaData.DESCARGA_TICKETS_INICIO, false);
        i.putExtras(extras);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(i);
        activity.finish();
    }

    // private void compruebaTickets throws
			/*
			 * Despues de consumir el servicio se cierra el progress dialog
			 */
    protected void onPostExecute(final Integer respuesta) {
        // desecha el dialogo antes de Terminar de cargar los productos
        pd.dismiss();

        if(respuesta == RESPUESTA_ERROR){

            AlertDialog aD = new AlertDialog.Builder(activity).create();
            aD.setTitle("Alert Dialog");

            // Setting Dialog Message
            aD.setMessage(mensajeError);
            aD.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    irInicioFlujo();
                }
            });
            aD.show();
        }else{
            irInicioFlujo();
        }

    }
}


