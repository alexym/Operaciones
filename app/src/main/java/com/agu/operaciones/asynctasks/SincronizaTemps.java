package com.agu.operaciones.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.FileCameraManager;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.utilities.SessionManager;
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
public class SincronizaTemps extends AsyncTask<String, Integer, Boolean> {
    private Activity activity;

    private ProgressDialog pd;

    private String mensajeError = "";
    private String numTicket;
    private String ticketBack;
    private String numTempTicket,etapa;


    private File directory;
    private File[] files;
    private FileCameraManager fileCamera;
    private String pathTicket;
    private JSONObject params = new JSONObject();
    private int _idReporte;
    private String myTemp;
    private int rowID;

    private static final String TAG = "SincronizaTemps";

    public SincronizaTemps(String numTicketSinc, Activity act){
        this.activity = act;
        this.numTicket = numTicketSinc;
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

    /*
     * METODO QUE TOMA LOS VALORES DE LA BASE DE DATOS INTERNA, LOS FORMATEA EN UN JSON Y LOS ENVIA MEDIANTE EL WEBSERVICE SMINSTICkET
     */
    private boolean enviaTicket(Cursor TicketCursor) throws JSONException,NoInternetException {

        Log.d("ADX2099", "Proceso de subida de Ticket temporal");
        boolean resp;
        JSONObject reporte = new JSONObject();
        JSONArray lista = new JSONArray();

        //Reporte
        reporte.put("Motivo",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Motivo)));
        reporte.put("Dependencia",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Dependencia)));
        reporte.put("Grupo", TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Grupo)));

        //Ubicación del reporte
        reporte.put("Tramo",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Tramo)));
        reporte.put("Calle",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Calle)));
        reporte.put("Colonia",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Colonia)));
        reporte.put("Delegacion",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Delegacion)));
        reporte.put("CP",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_CP)));
        reporte.put("Sentido",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Sentido)));
        reporte.put("LugarFisico",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_LugarFisico)));
        reporte.put("Localizado",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Localizado)));
        reporte.put("EntreCalle1",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_EntreCalle1)));
        reporte.put("PuntoReferencia",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_PuntoReferencia)));
        reporte.put("Vialidad",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Vialidad)));

        //Asignación
        reporte.put("DirGral",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_DirGral)));
        reporte.put("DirArea",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_DirArea)));
        reporte.put("Area",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Area)));
        reporte.put("GpoServicios",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_GrupoServicios)));
        reporte.put("Servicio",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Servicio)));
        reporte.put("ComentariosSI",TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_ComentarioSI)));
        reporte.put("Procede","SI");
        reporte.put(TicketTable.KEY_PrioridadSI,"Programado");

        String imagenesString = TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_uploadImageResponse));
        lista = new JSONArray(imagenesString);

        reporte.put("lImgs", lista);
        SessionManager sm = new SessionManager(activity);
        HashMap<String, String> datosUsuario = sm.getDetallesSession();
        reporte.put(SessionManager.IDSUP,datosUsuario.get(SessionManager.IDSUP));

        JSONObject completo = new JSONObject();
        completo.put("Tickets",reporte);

        Log.d(TAG, " enviando para sincronización ... " + completo.toString());


        JSONObject SMinsTemp = DataWebservice.callService(DataWebservice.SMINSTICKET, completo, activity);


        Log.d(" SMINsTicket", SMinsTemp.toString());
        String respuestaError = SMinsTemp.getString("Error");

        JSONObject actualizacion = new JSONObject();
        if(respuestaError.equals("false")){
            mensajeError = SMinsTemp.getString(TicketTable.KEY_NumTicket);
            Log.d("ADX2099","NO HUBO ERROR RIFO "+mensajeError);
            ticketBack = SMinsTemp.getString("NumTicket");
            myTemp = TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_NumTicket));
            String key;
            String ticketo;
            key = TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_Sincronizado));
            ticketo = TicketCursor.getString(TicketCursor.getColumnIndex(TicketTable.KEY_NumTicket));
            _idReporte = TicketCursor.getInt(TicketCursor.getColumnIndex(TicketTable.KEY_ROWID));
            Log.d("ADX2099", "CUAL ES EL NOMBRE MI TICKET TEMPORARL" + myTemp);
            Log.d("ADX2099", "CUAL ES EL ESTADO SINCRONIZADO?? " + key);
            Log.d("ADX2099", "CUAL ES EL NUMERO DE TICKET:..." + ticketo);
            Log.d("ADX2099", "CUAL ES EL ROW ID" + _idReporte);


            FileCameraManager fcm = new FileCameraManager(activity, myTemp,FileCameraManager.path_reporte);

            if(fcm.renameTempDirectory(SMinsTemp.getString(TicketTable.KEY_NumTicket))){
                Log.d("ADX2099","Se ha renombrado la carpeta");
                actualizacion.put(TicketTable.KEY_NumTicket, ticketBack);
                actualizacion.put(TicketTable.KEY_Sincronizado, TicketMetaData.CONST_SINCRONIZADO_SI);
                ContentValues cv = jsonToCV(actualizacion);
                ContentResolver cr = activity.getContentResolver();
                cr.update(TicketTable.CONTENT_URI,cv,null,null);


                Log.d("ADX2099","EL ROW ID:::" + rowID + " ACTUALIZACION:::." + actualizacion.toString() );

                sendImageToServer(mensajeError);
            }else{
                Log.d("ADX2099","Hubo un error mostrar alerta o toast");

            }
            resp = true;

        }else{
            //Nos regresa un error el servicio
            mensajeError = SMinsTemp.getString("ErrorMsg");
            Log.d("ADX2099","HUBO BRONCA AGUAS:: "+mensajeError);
            resp = false;
        }

        return resp;
    }


    private void sendImageToServer(String numTicket) throws JSONException{
        String etapaKey = "";
        fileCamera = new FileCameraManager(activity, numTicket, FileCameraManager.path_reporte);
        etapaKey = DataWebservice.IMAGE_SUP;
        pathTicket = fileCamera.path;
        directory = new File(pathTicket);
        files = directory.listFiles();
        HashMap<String , File>  imagenesSDCard = new HashMap<String, File>();
        if (files.length > 0){
            Log.d("ADX2099","LA CARPETA NO ESTA VACIA");
        }else{
            Log.d("ADX2099","LA CARPETA  ESTA bien  VACIA");
        }
        try{
            for (int i = 0; i < files.length; i++) {
                imagenesSDCard.put(files[i].getName(), files[i]);
                Log.d("ADX2099","ENTRA AL CICLO Y PONE EL ARCHIVO CON EL NOMBRE");
            }
            for(int i=0; i<files.length; i++){
                Log.d("ADX2099","ENTRA AL SEGUNDO CICLO para ");
                try{
                    File imagen = imagenesSDCard.get("img" + (1+i) + ".jpg");
                    Log.d("ADX2099","que imagen se enviara: "+imagen.getName());
                    String url=DataWebservice.uploadImage(imagen, "si" + numTicket + "-" + (i+1) + ".jpg", etapaKey);
                    Log.i("que nos devuelve url", url);
                }catch(SocketException sockExc){
                    Log.d("SendImageToServer",sockExc.getMessage());
                    mensajeError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                }
            }

        }catch(SocketTimeoutException toe){
            Log.d("ADX2099SincronizaTemps", "Se ha agotado el tiempo de espera : "+toe.getMessage());

        }
    }







    protected Boolean doInBackground(String... parametros){
        boolean respuesta = false;
        Cursor ticketCursor;
        ContentResolver cr = activity.getContentResolver();
        Log.d(TAG,"EL CONTENT RESOLVER...::" + cr);
        ticketCursor = cr.query(TicketMetaData.TicketTable.CONTENT_URI,null,TicketTable.KEY_NumTicket + " = ?",new String[]{numTicket},null);

        try{

            if(ticketCursor.moveToFirst()){
                rowID = ticketCursor.getInt((ticketCursor.getColumnIndex(TicketTable.KEY_ROWID)));
                String etapa = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Etapa));
                String estadoSincronizacion = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Sincronizado));
                respuesta = enviaTicket(ticketCursor);


                String key;
                key = ticketCursor.getString(ticketCursor.getColumnIndex(TicketTable.KEY_Sincronizado));

                Log.d("ADX2099", "EL VALOR DEL TICKET SINcronizado::..." + key);

            }
        }catch(JSONException e){
            respuesta = false;
            mensajeError = e.getMessage();
            Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();
        }catch(NoInternetException nie){
            respuesta = false;
            mensajeError = nie.getMessage();
            Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();
        }finally{
            if (ticketCursor != null)
                ticketCursor.close();

        }




        return respuesta;
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
     * Despues de consumir el servicio se cierra el progress dialog
     */
    protected void onPostExecute(final Boolean respuesta) {
        // desecha el dialogo antes de Terminar de cargar los productos
        pd.dismiss();
        System.out.println("Respuesta del servicios de sincronizacion: " + mensajeError);
        if (!respuesta) {
            mensajeError = mensajeError;
        }else{

            AlertDialog alert = new AlertDialog.Builder(activity).create();
            alert.setTitle("Ticket Insertado Correctamente");
            alert.setMessage("NumTicket : " + mensajeError);

            alert.setButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                }

                ;
            });
            alert.show();
            mensajeError ="El Ticket fue insertado con el numero " + this.numTicket + " sincronizado correctamente.";

        }


        Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

    }


}