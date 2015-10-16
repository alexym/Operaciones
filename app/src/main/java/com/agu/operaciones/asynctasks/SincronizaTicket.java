package com.agu.operaciones.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.FileCameraManager;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.webservices.DataWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Cloudco on 14/10/15.
 */
public class SincronizaTicket extends AsyncTask<String,Integer,Boolean> {

    private Activity activity;

    private ProgressDialog pd;
    private String mensajeError = "";
    private String numTicket;
    private int rowID;

    private File directory;
    private File[] files;
    private FileCameraManager fileCamera;
    private String pathTicket;
    private JSONObject params = new JSONObject();

    private static final String TAG = "Sincroniza Ticket";
    private ContentResolver contentResolver;
    Uri uriBase;



    public SincronizaTicket(String numTicketSinc, Activity act) {

        this.activity = act;
        this.numTicket = numTicketSinc;

    }


    private  boolean enviaTicket(Cursor ticketCursor) throws JSONException,NoInternetException {
		/*
		 * PROCESO DE SUBIDA DEL TICKET y sus im�genes al webservice
		 */
        //pd.setMessage("Sincronizando Ticket " + this.numTicket + "...");
        Log.d(TAG, "Enviando Ticket de sincronizacion");
        boolean resp = false;
        JSONObject reporte = new JSONObject();
        JSONObject completo = new JSONObject();
        JSONArray lista = new JSONArray();

        reporte.put("Procede", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Procede)));
        reporte.put("PrioridadSI", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_PrioridadSI)));
        reporte.put(TicketMetaData.TicketTable.KEY_Area,ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Area)));
        reporte.put("DirArea", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_DirArea)));
        reporte.put("DirGral", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_DirGral)));
        reporte.put("GpoServicios", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_GrupoServicios)));
        reporte.put("Servicio", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Servicio)));
        reporte.put(TicketTable.KEY_atendido, ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_atendido)));
        reporte.put("ComentariosSF", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_ComentariosSF)));
        reporte.put("ComentariosSI", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_ComentarioSI)));
        reporte.put("Etapa", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa)));
        reporte.put("IdTicket", ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_IdTicket)));
        reporte.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_SI);


        String imagenesString = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_uploadImageResponse));

        lista = new JSONArray(imagenesString);

        reporte.put("lImgs", lista);
        completo.put("Tickets", reporte);

        Log.d(TAG, " enviando para sincronización ... " + completo.toString());
        JSONObject SMins = DataWebservice.callService(DataWebservice.SMACTTICKET, completo, activity);

        Log.i(TAG, SMins.toString());


        JSONObject actualizacion = new JSONObject();
        if (SMins.getString("Error").equals("")){
            resp = true;

            actualizacion.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_SI);
            ContentValues cv = jsonToCV(actualizacion);
            ContentResolver cr = activity.getContentResolver();
            uriBase= ContentUris.withAppendedId(TicketTable.CONTENT_URI, rowID);
            cr.update(uriBase,cv,null,null);
        }else {
            // Nos regresa error el webservice
            mensajeError = SMins.getString("ErrorMsg");
            resp = false;
        }

        return resp;
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
                Log.e(TAG,"->creaTicket()"+ e.getMessage());
                e.printStackTrace();
            }


        }
        return cv;
    }

    private boolean enviaImagen(String etapa) {
        String etapaKey = "";
        String preTicket = "";


//        if (etapa.equals(TicketMetaData.CONST_ETAPA_CONCLUSION)) {
//            fileCamera = new FileCameraManager(activity, numTicket,FileCameraManager.path_supervisor);
//            etapaKey = DataWebservice.IMAGE_CONCL;
//            preTicket = "sf";
//
//        }else{
            fileCamera = new FileCameraManager(activity,numTicket,FileCameraManager.path_supervisor);
            etapaKey = DataWebservice.IMAGE_SUP;
            preTicket = "sf";
//        }


        pathTicket = fileCamera.path;

        directory = new File(pathTicket);
        Log.d(TAG,"El path...:" + pathTicket);

        files = directory.listFiles();


        HashMap<String , File> imagenesSDCard = new HashMap<String, File>();
        for (int i = 0; i < files.length; i++) {
            imagenesSDCard.put(files[i].getName(), files[i]);
        }
        if (files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File imagen = imagenesSDCard.get("img" + (i + 1) + ".jpg");
                String url = "";
                Log.d(TAG,"las imagenes son: "
                        + imagen.getName());
                try {
                    url = DataWebservice.uploadImage(imagen, preTicket
                            + numTicket + "-" + (i + 1) + ".jpg", etapaKey);
                } catch (SocketException sockExc) {
                    Log.d("SendImageToServer", sockExc.getMessage());
                    mensajeError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    // respuesta = false;
                } catch (SocketTimeoutException sockTm) {
                    Log.d("SendImageToServer", sockTm.getMessage());
                    mensajeError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    // respuesta = false;
                }

                // control de subida de imagen
				/*
				 * Comprobamos que la url devuelta por el servidor sea una
				 * cadena no vacia, en caso contrario , nos indica que la imagen
				 * no ha sido subida correctamente
				 */
                if (url.length() == 0) {
                    mensajeError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    return false;
                }
            }
        } else {
            Log.d(TAG,"no hay imagenes");
            mensajeError = "No se han encontrado imágenes del ticket para enviar";

            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        // Corre en el UI thread
        super.onPreExecute();
        pd = new ProgressDialog(activity);
        pd.setMessage("Sincronizando Ticket " + this.numTicket + "...");
        pd.show();

    }

	/*
	 * Realiza el guardado del ticket en la base de datos y si hay internet,
	 * intenta enviar el ticket con el servidor
	 */

    @Override
    protected Boolean doInBackground(String... parametros) {

        boolean respuesta = false;
        Cursor ticketCursor;
        ContentResolver cr = activity.getContentResolver();
        Log.d(TAG,"EL CONTENT RESOLVER...::" + cr);
        //String[] projection = {TicketTable.KEY_ROWID, TicketTable.KEY_IdTicket,TicketTable.KEY_NumTicket,TicketTable.KEY_Etapa};

        ticketCursor = cr.query(TicketTable.CONTENT_URI,null,TicketTable.KEY_NumTicket + " = ?",new String[]{numTicket},null);

        try {

            if (ticketCursor.moveToFirst()) {
                rowID = ticketCursor.getInt((ticketCursor.getColumnIndex(TicketTable.KEY_ROWID)));
                String etapaTicket = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa));
                if (enviaImagen(etapaTicket)) {

                    respuesta = enviaTicket(ticketCursor);

                }
            } else {
                respuesta = false;
            }

        } catch (JSONException e) {
            respuesta = false;
            mensajeError = e.getMessage();
        } catch (NoInternetException nie) {
            respuesta = false;
            mensajeError = nie.getMessage();
        } finally {

        }

    /*
        try {

            if (ticketCursor.moveToFirst()) {
                rowID = ticketCursor.getInt((ticketCursor.getColumnIndex(TicketTable.KEY_ROWID)));
                String etapaTicket = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa));
                if (enviaImagen(etapaTicket)) {
                    respuesta = enviaTicket(ticketCursor);

                }else{
                    mensajeError = "Ticket previamente sincronizado";
                }
            } else {
                respuesta = false;
            }

        } catch (JSONException e) {
            respuesta = false;
            mensajeError = e.getMessage();


        } catch (NoInternetException nie) {
            respuesta = false;
            mensajeError = nie.getMessage();

        } finally {

        }

        /*dbAdapter = new TicketTable(activity);
        dbAdapter.open();

        Cursor ticketCursor = null;

        try {

            ticketCursor = dbAdapter.obtenerTicket(new String[] { "*" },TicketsAdapter.KEY_NumTicket, this.numTicket);
            if (ticketCursor.moveToFirst()) {
                rowID = ticketCursor.getInt((ticketCursor.getColumnIndex(TicketsAdapter.KEY_ROWID)));
                String etapaTicket = ticketCursor.getString(ticketCursor.getColumnIndex(TicketsAdapter.KEY_Etapa));
                if (enviaImagen(etapaTicket)) {
                    respuesta = enviaTicket(ticketCursor);

                }else{
                    mensajeError = "Ticket previamente sincronizado";
                }
            } else {
                respuesta = false;
            }

        } catch (JSONException e) {

            respuesta = false;
            mensajeError = e.getMessage();

        } catch (NoInternetException nie) {
            respuesta = false;
            mensajeError = nie.getMessage();
        } finally {
            if (ticketCursor != null)
                ticketCursor.close();
            dbAdapter.close();

        }
        */
        return respuesta;
    }

    // private void compruebaTickets throws
	/*
	 * Despues de consumir el servicio se cierra el progress dialog
	 */
    protected void onPostExecute(final Boolean respuesta) {
        // desecha el dialogo antes de Terminar de cargar los productos

        pd.dismiss();
        Log.d(TAG,"Respuesta del servicios de sincronizacion: " + mensajeError);
        if (!respuesta) {
            mensajeError = mensajeError;
        }else{

            mensajeError ="Ticket " + this.numTicket + " sincronizado correctamente.";
            AlertDialog alert = new AlertDialog.Builder(activity).create();
            alert.setTitle("¡Listo!");
            alert.setMessage("El Ticket se sincronizó correctamente ");

            alert.setButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {


                };
            });
            alert.show();

        }


        Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

    }



}

