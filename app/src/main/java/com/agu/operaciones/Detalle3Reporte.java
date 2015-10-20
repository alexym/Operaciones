package com.agu.operaciones;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.agu.operaciones.asynctasks.ActualizaTicket;
import com.agu.operaciones.fragments.FotosYComentario;
import com.agu.operaciones.fragments.FotosYComentarioNoEditable;
import com.agu.operaciones.fragments.SpinnersSupervisionEditable;
import com.agu.operaciones.fragments.SpinnersSupervisionNoEditable;
import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.ConfiguracionGPS;
import com.agu.operaciones.utilities.FileCameraManager;
import com.agu.operaciones.webservices.DataWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Detalle3Reporte extends ActionBarActivity implements FotosYComentario.OnFragmentInteractionListener, SpinnersSupervisionEditable.OnFragmentInteractionListenerSpinnerSE, FotosYComentarioNoEditable.OnFragmentInteractionListener, SpinnersSupervisionNoEditable.OnFragmentInteractionListener, View.OnClickListener {
    private static final int CAMERA_REQUEST = 1888;

    public static final String TAG = "ReporteActivity";
    public final String MATERIALES_TAG = "materiales";


    private FileCameraManager fileCamera;
    private ImageButton selected;
    private Uri imageUri;
    private Button siguiente, atrasButton;

    private String pathTicket;
    private String numTicket, etapa, prioridadSI, estadoSincronizado, comentarioSI, procedeValue;
    private int row_id;

    private String currentImageTag;

    // Valores por default para estos campos
    private String comentario = "";

    private String estadoOperacion = "Si";
    private String materiales = "";
    private String cantidad = "";
    private String unidadMedida = "";

    // Url imagenes tomadas
    private String[] imagenesUrl = new String[3];

    private File directory;
    private File[] files;


    private HashMap<String, Boolean> validaciones;
    private HashMap<String, Boolean> nuevaValidacion;


    private Bundle extras;


    private SharedPreferences dataImageTicket;
    private SharedPreferences.Editor editor;

    private ConfiguracionGPS cGPS;

    private TextView tituloText, subtituloText, prioridadText, procedeText, tituloProcedeAtendidoText, tituloPrioridadText;
    Uri uriBase;


    ContentResolver cresolver;
    FotosYComentario fotosYComentarioFragment;
    FotosYComentarioNoEditable fotosYComentarioFragmentNoEditable;
    SpinnersSupervisionEditable spinnersSupervisionEditableFragment;
    SpinnersSupervisionNoEditable spinnersSupervisionNoEditableFragment;

    private JSONObject params2 = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        setContentView(R.layout.activity_detalle3_reporte);
        //instancia de gps
        cGPS = new ConfiguracionGPS(this, false);
        //Inicia valores
        initData();
        initUI();

    }

    public void initData() {
        extras = this.getIntent().getExtras();
        numTicket = extras.getString(TicketTable.KEY_NumTicket);
        row_id = extras.getInt(TicketTable.KEY_ROWID);
        estadoSincronizado = extras.getString(TicketTable.KEY_Sincronizado);
        etapa = extras.getString(TicketTable.KEY_Etapa);
        cresolver = Detalle3Reporte.this.getContentResolver();
        uriBase = ContentUris.withAppendedId(TicketTable.CONTENT_URI, row_id);
            /*
             * OPERACION DE VALIDACION DE TICKETS DESCARGADOS
			 */
        String[] projection = {TicketTable.KEY_ComentariosOp, TicketTable.KEY_lImgSI, TicketTable.KEY_EstatusOp, TicketTable.KEY_Materiales, TicketTable.KEY_Cantidad, TicketTable.KEY_UDeMedida};
        Uri uriBase = ContentUris.withAppendedId(TicketTable.CONTENT_URI, row_id);
        Cursor ticketsCursor = cresolver.query(uriBase, projection, null, null, null);


        if (ticketsCursor.moveToFirst()) {
            comentario = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_ComentariosOp));
            estadoOperacion = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_EstatusOp));
            materiales = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Materiales));
            cantidad = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Cantidad));
            unidadMedida = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_UDeMedida));

            Log.i(TAG, ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_lImgSI)));
            try {

                JSONArray jsonArrayImg = new JSONArray(
                        ticketsCursor.getString(ticketsCursor
                                .getColumnIndex(TicketTable.KEY_lImgSI)));
                for (int i = 0; i < jsonArrayImg.length(); i++) {

                    JSONObject jsonObject = jsonArrayImg
                            .getJSONObject(i);
                    imagenesUrl[i] = jsonObject
                            .getString(TicketTable.KEY_ImgSI);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "error con jsonObject al obtener imagenes enviadas" +
                        e.getMessage());

            }


        }
        /*
		 * Validaciones es una estructura de datos de tipo HashMap con la estructura clave y boleano
		 */
        validaciones = new HashMap<String, Boolean>();
        validaciones.put(TicketTable.KEY_ComentarioSI, false);
        validaciones.put(TicketTable.KEY_uploadImageResponse, false);
        validaciones.put(MATERIALES_TAG, false);
        //Nueva validacion perteneciente al flujo de cancelado
        nuevaValidacion = new HashMap<String, Boolean>();
        nuevaValidacion.put(TicketTable.KEY_ComentarioSI, false);
        //Declaracion de archivo el cual tiene la ruta que almacenará las fotos
        fileCamera = new FileCameraManager(Detalle3Reporte.this, numTicket, FileCameraManager.path_supervisor);
        pathTicket = fileCamera.path;

        dataImageTicket = getSharedPreferences(TicketMetaData.SP_TICKET, MODE_PRIVATE);
        editor = dataImageTicket.edit();
        directory = new File(pathTicket);
        files = directory.listFiles();
        if (files.length > 0) {
            validaciones.put(TicketTable.KEY_uploadImageResponse, true);
        }
    }

    public void initUI() {
        if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)) {
            FotosYComentario fragment = FotosYComentario.newInstance(pathTicket, comentario);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fotos_y_comentario_conclusion, fragment, FotosYComentario.TAG);
            ft.commit();
            //fotosYComentarioFragment = (FotosYComentario) getFragmentManager().findFragmentById(R.id.fotos_y_comentario);

            SpinnersSupervisionEditable fragment2 = SpinnersSupervisionEditable.newInstance(estadoOperacion, materiales, cantidad, unidadMedida);
            FragmentTransaction ft2 = getFragmentManager().beginTransaction();
            ft2.replace(R.id.spinners_supervision_editables_conclusion, fragment2, SpinnersSupervisionEditable.TAG);
            ft2.commit();
            //spinnersSupervisionEditableFragment = (SpinnersSupervisionEditable) getFragmentManager().findFragmentById(R.id.spinners_supervision_editables);
        } else {
            FotosYComentarioNoEditable fragment = FotosYComentarioNoEditable.newInstance(pathTicket, comentario, imagenesUrl, estadoSincronizado);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fotos_y_comentario_conclusion, fragment, FotosYComentarioNoEditable.TAG);
            ft.commit();
            //fotosYComentarioFragmentNoEditable = (FotosYComentarioNoEditable) getFragmentManager().findFragmentById(R.id.fotos_y_comentario);

            SpinnersSupervisionNoEditable fragment2 = SpinnersSupervisionNoEditable.newInstance(estadoOperacion, materiales, cantidad, unidadMedida);
            FragmentTransaction ft2 = getFragmentManager().beginTransaction();
            ft2.replace(R.id.spinners_supervision_editables_conclusion, fragment2, SpinnersSupervisionEditable.TAG);
            ft2.commit();
            //spinnersSupervisionNoEditableFragment = (SpinnersSupervisionNoEditable) getFragmentManager().findFragmentById(R.id.spinners_supervision_editables);

        }
        siguiente = (Button) findViewById(R.id.siguienteBtn_conclusion);
        if(estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_SI)){
            siguiente.setBackground(getDrawable(R.drawable.btn_inicio));
        }
        atrasButton = (Button) findViewById(R.id.backbutton_reporte_conclusion);
        subtituloText = (TextView) findViewById(R.id.tituloNumTicket4);
        subtituloText.setText("Ticket: " + numTicket);
        atrasButton.setOnClickListener(this);
    }

    //Listener que recibe los valores de validacion de imagenes y comentario del fragment "FotosYComentario"
    @Override
    public void onFragmentInteraction(Bundle bundle) {
        comentario = bundle.getString(FotosYComentario.COMENTVAL);
        if (comentario.length() == 0) {
            validaciones.put(TicketTable.KEY_ComentarioSI, false);
            nuevaValidacion.put(TicketTable.KEY_ComentarioSI, false);
        } else {
            validaciones.put(TicketTable.KEY_ComentarioSI, true);
            nuevaValidacion.put(TicketTable.KEY_ComentarioSI, true);
        }
        //validaciones.put(TicketTable.KEY_uploadImageResponse, bundle.getBoolean(FotosYComentario.IMGVAL));
        Log.i(TAG, "Que nos trae validaciones " + comentario + " " + validaciones.toString());

    }

    @Override
    public void onFragmentInteractionFyCNoeditable(Uri uri) {

    }

    @Override
    public void onFragmentInteractionSpinnerNoEdit(Uri uri) {

    }

    @Override
    public void onFragmentInteractionSSE(Bundle bundle) {
        estadoOperacion = bundle.getString(TicketTable.KEY_EstatusOp);
        materiales = bundle.getString(TicketTable.KEY_Materiales);
        cantidad = bundle.getString(TicketTable.KEY_Cantidad);
        unidadMedida = bundle.getString(TicketTable.KEY_UDeMedida);
        if(materiales == null) {
            materiales = "";
        }
            if (materiales.length() > 0) {
                validaciones.put(MATERIALES_TAG, true);
            } else {
                validaciones.put(MATERIALES_TAG, false);
            }

        Log.i(TAG, "el spinner es" + estadoOperacion + " " + materiales + " " + cantidad + " " + unidadMedida);
    }

    public void takePicture(View view) {
        //Se verifica que la latitud y la longitud sea deferente de 0 debido a que estos valores son necesarios para el envio de Ticket
        if (cGPS.getLatitude() != 0 && cGPS.getLongitude() != 0) {
            //Se evalua que el Ticket no este sincronizado mediante una Constante, esta constante se encuentra declarada en el Adaptador
            if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)) {
                if (view instanceof ImageButton) {
                    selected = (ImageButton) view;
                    currentImageTag = (String) selected.getTag();
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = fileCamera.getOutputMediaFileUri(currentImageTag);
                    intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }
        } else {
            Toast.makeText(this, "Es necesario contar con latitud y longitud", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + imageUri.toString());
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                new resizeBitmapImage().execute(imageUri);
                // Valida que hayan imagenes
                files = directory.listFiles();
                if (files.length > 0) {
                    validaciones.put(TicketTable.KEY_uploadImageResponse, true);
                } else {
                    validaciones.put(TicketTable.KEY_uploadImageResponse, false);

                }
                // Inserta latitud y longitud de imagenes de sharedPreferences
                String currentSP = dataImageTicket.getString(extras.getString(TicketTable.KEY_NumTicket), "");
                JSONObject objLoc = new JSONObject();
                JSONObject objImg;
                try {
                    objLoc.put(TicketMetaData.LATITUD, cGPS.getLatitude());
                    objLoc.put(TicketMetaData.LONGITUD, cGPS.getLongitude());
                    if (currentSP.length() > 0) {
                        objImg = new JSONObject(currentSP);
                    } else {
                        objImg = new JSONObject();
                    }
                    objImg.put(currentImageTag, objLoc);
                    editor.putString(
                            extras.getString(TicketTable.KEY_NumTicket),
                            objImg.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "onActivityResult Exception");
                e.printStackTrace();
            }


        }
    }

    private class resizeBitmapImage extends AsyncTask<Uri, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Uri... params) {
            Uri uriToResize = params[0];
            Log.i(TAG, "el uri en doInBackground es :" + uriToResize.getPath());
            Bitmap tempBitPant = null;
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(uriToResize), null, o);
                //The new size we want to scale to
                final int REQUIRED_WIDTH = 400;
                final int REQUIRED_HIGHT = 400;
                //Find the correct scale value. It should be the power of 2.
                int scale = 1;
                while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                    scale *= 2;
                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                tempBitPant = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriToResize), null, o2);
                File f = new File(uriToResize.getPath());
                f.createNewFile();
                Log.i(TAG, "resizeBitmap --> despues del createNewFile");
                // Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                tempBitPant.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();
                Log.i(TAG, "resizeBitmap --> despues del despues de convertir el bitmap a array");
                // write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                Log.i(TAG, "resizeBitmap --> despues del despues de escribir");
                fos.close();
                //tempBit.recycle();
                return tempBitPant;


            } catch (FileNotFoundException e) {

                e.printStackTrace();
                Log.e("onActivityResult", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e(TAG, "-> Asyinck task --- NullPointerException");
                e.printStackTrace();
                Toast.makeText(Detalle3Reporte.this, "Andale", Toast.LENGTH_SHORT).show();
                //AlertDialog alert = new AlertDialog.Builder(ReporteActivity.this).create();
                //alert.setTitle("Atencion");
                //alert.setMessage("Intenta nuevamente capturar la foto");
                //alert.show();
            }

            return tempBitPant;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            selected.setImageBitmap(result);
            Log.i(TAG, "si llega al onPost");
        }

    }

    public boolean imagenesYaEnviadas() {
        String[] projection = {TicketTable.KEY_uploadImageResponse};
        uriBase = ContentUris.withAppendedId(TicketTable.CONTENT_URI, row_id);
        Cursor resultSet = cresolver.query(uriBase, projection, null, null, null);
        boolean imgEnviadas = false;
        while (resultSet.moveToNext()) {
            String uploadText = resultSet.getString(resultSet
                    .getColumnIndex(TicketTable.KEY_uploadImageResponse));

            if (!uploadText.trim().equals(""))
                imgEnviadas = true;
        }
        return imgEnviadas;
    }

    public void siguienteActivity(View v) {
        if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)) {
					/*
					 * Actualizamos el valor del comentario en la base de datos
					 * asi como los valores de procede, prioridad y atendido
					 */
            //Se evalua si el comentario se obtuvo
            if (actualizaComentario())
                Log.i("ReporteActivity", "Comentario actualizado");

						/*
						 * Se realizan las validaciones de las reglas de flujo siguientes
						 * 1.-El ticket forzosamente debe de tener una imagen
						 * 2.-El ticket debe de contar con un comentario
						 *
						 * En caso de que el ticket sea cancelado el usuario podra continuar solo con el comentario y ya no enviará imágenes
						 */

            if (validaciones.containsValue(false)) {
                Toast.makeText(Detalle3Reporte.this, "Necesitas Ingresar un comentario, la cantidad de Material y al menos una fotografia", Toast.LENGTH_SHORT).show();

                // entonces no esta validado
            } else {
                // Comprobamos que no sean enviadas nuevamente
                if (imagenesYaEnviadas())
                    navegarSiguienteActivity();
                else {
                    new ServiceSendImage(Detalle3Reporte.this).execute("");
                }

            }


        } else if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_NO)) {
            //Esta seccion entra cuando existe un error de sincronizacion por Internet

            if (imagenesYaEnviadas()) {
                navegarSiguienteActivity();
            } else {
                new ServiceSendImage(Detalle3Reporte.this).execute("");
            }
        } else if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_SI)) {
            //ADX2099 : PANTALLAS ESTATICAS
            //En caso de que el ticket ya se encuentre sincronizado se evalua si esta en etapa de supervision o conclusion

            //Se revisa el campo de prioridad para saber si viene cancelado o no
            Intent i = new Intent(getApplicationContext(),
                    TicketListActivity.class);
            Bundle extras = new Bundle();
            // para que no descargue al inicio de TicketActivity
            extras.putBoolean(TicketMetaData.DESCARGA_TICKETS_INICIO, false);
            i.putExtras(extras);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            Detalle3Reporte.this.finish();


        }
    }

    private class ServiceSendImage extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog pd = null;
        private String messageError;
        private JSONObject ticketUpdate = new JSONObject();
        Activity _context;

        public ServiceSendImage(Activity ctx) {
            _context = ctx;
            messageError = "Imgenes subidas correctamente al servidor";
        }

        @Override
        protected void onPreExecute() {
            // Corre en el UI thread
            super.onPreExecute();
            pd = ProgressDialog.show(Detalle3Reporte.this, "Aviso",
                    "Enviando Imagenes...", true);
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            boolean subidoAServer = false;

            // Checamos que posea conexi�n a internet para tratar de subir las
            // imagenes
            ConnectivityManager connManger = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManger.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                try {
                    subidoAServer = sendImageToServer();
                } catch (JSONException e) {

                    e.printStackTrace();
                    subidoAServer = false;
                } catch (SocketTimeoutException ste) {
                    subidoAServer = false;
                    messageError = "Tiempo agotado al intentar subir imagen. Intentalo más tarde";
                } catch (SocketException se) {
                    subidoAServer = false;
                    messageError = "Tiempo agotado al intentar subir imagen. Intentalo más tarde";
                }

            } else {
                subidoAServer = false;
                messageError = "No cuentas con acceso a internet, intentalo más tarde";
            }

            return subidoAServer;
        }

        private boolean sendImageToServer() throws JSONException,
                SocketTimeoutException, SocketException {

            boolean respuesta = true;
            JSONObject img = new JSONObject();
            JSONArray lista = new JSONArray();
            String urlCustom = "";

            files = directory.listFiles();
            String etapaKey = "";

            String preTicket = "";
            //todo modificar la liga del nombre del ticket

            urlCustom = DataWebservice.urlMap
                    .get(DataWebservice.URLCUSTOMOPERACION);
            etapaKey = DataWebservice.IMAGE_OPERACION;
            preTicket = "op";

            String currentSP = dataImageTicket.getString(
                    extras.getString(TicketTable.KEY_NumTicket), "");
            JSONObject objImg = new JSONObject(currentSP);
            System.out.println(currentSP);
            Iterator<?> keys = objImg.keys();
            ArrayList<String> llaves = new ArrayList<String>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                llaves.add(key);
            }

            HashMap<String, File> imagenesSDCard = new HashMap<String, File>();
            for (int i = 0; i < files.length; i++) {
                imagenesSDCard.put(files[i].getName(), files[i]);
            }

            for (int i = 0; i < files.length; i++) {
                String url = "";
                //count--;
                try {
                    File imagen = imagenesSDCard.get("img" + (i + 1) + ".jpg");
                    Log.d(TAG, "que imagen se enviara: "
                            + imagen.getName());
                    url = DataWebservice.uploadImage(imagen, preTicket
                            + numTicket
                            + "-" + (i + 1) + ".jpg", etapaKey);
                } catch (SocketException sockExc) {
                    Log.d("SendImageToServer", sockExc.getMessage());
                    messageError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    respuesta = false;
                } catch (SocketTimeoutException sockTm) {
                    Log.d("SendImageToServer", sockTm.getMessage());
                    messageError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    respuesta = false;
                }

                // control de subida de imagen
                /*
				 * Comprobamos que la url devuelta por el servidor sea una
				 * cadena no vacia, en caso contrario , nos indica que la imagen
				 * no ha sido subida correctamente
				 */
                if (url.length() == 0) {
                    messageError = "Se perdió la conexión a internet. Subir imágenes más tarde";
                    return false;
                }

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = sdf.format(date) + " " + date.getHours() + ":"
                        + date.getMinutes() + ":" + date.getSeconds();

                // String fecha = sdf.format(date) + "-" + date.getMonth() + "-"
                // + date.getDate() + " " + date.getHours() + ":"
                // + date.getMinutes() + ":" + date.getSeconds();
                // Obtener latitud y longitud por imagen
                // String imageTag =(String)images[i].getTag();
                JSONObject currentObj = new JSONObject(objImg.getString(llaves
                        .get(i)));
                img.put("FechaHora", fecha);
                //Todo aqui----------------------------------------------------
                img.put("Img", urlCustom + url);
                //img.put("Img", "hola soy una url de  imagen");
                img.put("Latitud", currentObj.getDouble(TicketMetaData.LATITUD));
                img.put("Longitud",
                        currentObj.getDouble(TicketMetaData.LONGITUD));
                lista.put(img);
                img = new JSONObject();

            }

            Log.i(TAG, "el objeto imagen es" + lista.toString());
            ContentValues cv = new ContentValues();
            cv.put(TicketTable.KEY_uploadImageResponse, lista.toString());
            cresolver.update(uriBase, cv, null, null);

            return respuesta;
        }

        protected void onPostExecute(final Boolean respuesta) {
            // desecha el dialogo antes de Terminar de cargar los productos
            pd.cancel();

            if (respuesta) {
                navegarSiguienteActivity();
            }

            Toast.makeText(_context, messageError, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {

        cGPS.removeUpdates();
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        cGPS.configuracionLocationManager();

    }

    @Override
    public void onClick(View v) {

        super.onBackPressed();
        if (actualizaComentario())
            Log.i("ReporteActivity", "comentario actualizado");
        else
            Log.i("ReporteActivity", "comentario no se pudo actualizar");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Guardamos el comentario en la base de datos
        if (actualizaComentario())
            Log.i("ReporteActivity", "comentario actualizado");
        else
            Log.i("ReporteActivity", "comentario no se pudo actualizar");
    }

    private boolean actualizaComentario() {
        // La funcion solo actualizara a la base mientras sea editable el ticket
        // (estado SINCRONIZADO_AUN_NO
        if (estadoSincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO)) {
            ContentValues cv = new ContentValues();
            cv.put(TicketTable.KEY_ComentariosOp, comentario);
            cv.put(TicketTable.KEY_EstatusOp, estadoOperacion);
            cv.put(TicketTable.KEY_Materiales, materiales);
            cv.put(TicketTable.KEY_Cantidad, cantidad);
            cv.put(TicketTable.KEY_UDeMedida, unidadMedida);

            Log.i(TAG, "update es" + comentario + " " + estadoOperacion + " " + materiales + " " + cantidad + " " + unidadMedida);
            cresolver.update(uriBase, cv, null, null);
            return true;
        } else {
            return false;
        }
    }

    private void navegarSiguienteActivity() {
        extras.putString(TicketTable.KEY_ComentariosSF, comentario);
        JSONObject params = new JSONObject();
        try {
            params.put(TicketTable.KEY_NumTicket, extras.getString(TicketTable.KEY_NumTicket));
            params.put(TicketTable.KEY_EstatusOp, estadoOperacion);
            params.put(TicketTable.KEY_Materiales, materiales);
            params.put(TicketTable.KEY_Cantidad, cantidad);
            params.put(TicketTable.KEY_UDeMedida, unidadMedida);
            params2.put(TicketTable.KEY_ROWID, extras.getInt(TicketTable.KEY_ROWID));
        } catch (JSONException e) {
            Log.e(TAG, "ReporteActivity siguienteButton.onClick Error al formar parametros " + e.getMessage());
            e.printStackTrace();
        }

			/*
			 * Actualizo el ticket en etapa de Conslusión (No hay necesidad de ir a la activity de Direcciones y Dependencias
			 * ya contiene estos datos de la base)
			 */
        //Mandamos a llamar a la clase Actualiza Ticket y pasamos por parametro el objeto JSON
        ActualizaTicket thread = new ActualizaTicket(Detalle3Reporte.this);
        thread.execute(params, params2);


    }

//    {
//        "Tickets":{
//                "IdTicket":"5001300000oqzJaAAI",
//                "EstatusOp":"Atendido",
//                "ComentariosOp":"prueba",
//                "Materiales":"maderozo",
//                "Cantidad":"1",
//                "UDeMedida":"gr",
//                "lImgs":[
                    //        {
                    //            "Longitud":"-99.05841842",
                    //                "Latitud":"19.46467636",
                    //                "Img":"https://assets-cdn.github.com/images/modules/logos_page/Octocat.png",
                    //                "FechaHora":"2015-06-26 00:04:29"
                    //        },
                    //        {
                    //            "Longitud":"-99.05819014",
                    //                "Latitud":"19.46514425",
                    //                "Img":"http://i.stack.imgur.com/oaWJU.png",
                    //                "FechaHora":"2015-06-26 00:04:27"
                    //        }
                //        ]
        //    }
//    }


}

