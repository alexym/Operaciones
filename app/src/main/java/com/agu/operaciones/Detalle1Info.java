package com.agu.operaciones;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.ImageCache.ImageLoader;

public class Detalle1Info extends AppCompatActivity implements View.OnClickListener {
    private String currentIdTicket, etapaSupervision ,urlImage, currentNumTicket;
    Cursor ticketsCursor;

    private TextView tituloEtapaText,tituloDetailText, motivoText, descripcionText, vialidadText, tramoText, calleText, coloniaText, delegacionText, cpText,
            sentidoText, lugarFisicoText, localizadoText,entreCalle1,entreCalle2,puntoDeReferencia;
    private Button botonMapa,botonAtras;
    private String imgIng1,imgIng2,imgIng3;
    private ImageView img1, img2, img3;
    private String NOTIFICACION = "Supervisón";
    String TAG = "Detalle1Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        setContentView(R.layout.activity_detalle1_info);
        // Obteniendo el valor por la anterior Activity
        Intent i = getIntent();
        currentIdTicket = i.getStringExtra(TicketMetaData.TicketTable.KEY_IdTicket);
        etapaSupervision = i.getStringExtra(TicketTable.KEY_EtapaSupervision);
        currentNumTicket = i.getStringExtra(TicketTable.KEY_NumTicket);
        Log.i(TAG, "el id ticket es" + currentIdTicket);
        initUIElements();

    }
    private void initUIElements() {


        this.setTitle(currentNumTicket);
        // SE INSTANCIAN LOS TEXTVIEW DE LA INTERFAZ

        tituloEtapaText = (TextView) findViewById(R.id.tituloEtapa_textView);

        if(NOTIFICACION.equals(etapaSupervision)){
            tituloEtapaText.setText("     Supervisón");
        }else{
            tituloEtapaText.setText("Verificación Inicial");
        }

        tituloDetailText = (TextView) findViewById(R.id.tituloDetail_textView);
        tituloDetailText.setText("Ticket: " + currentNumTicket);
        motivoText = (TextView)findViewById(R.id.motivo_textView);
        descripcionText = (TextView) findViewById(R.id.descripcion_textView);
        vialidadText = (TextView) findViewById(R.id.vialidad_textView);
        tramoText = (TextView) findViewById(R.id.tramo_textView);
        calleText = (TextView) findViewById(R.id.calle_textView);
        coloniaText = (TextView) findViewById(R.id.colonia_textView);
        delegacionText = (TextView) findViewById(R.id.delegacion_textView);
        cpText = (TextView) findViewById(R.id.cp_textView);
        sentidoText = (TextView) findViewById(R.id.sentido_textView);
        lugarFisicoText = (TextView) findViewById(R.id.lugarFisico_textView);

        localizadoText = (TextView) findViewById(R.id.localizado_textView);
        entreCalle1 =(TextView) findViewById(R.id.entreCalle1S_textView);
        entreCalle2 =(TextView) findViewById(R.id.entreCalle2S_textView);
        puntoDeReferencia = (TextView) findViewById(R.id.puntoDeReferenciaS_textView);

        botonMapa = (Button) findViewById(R.id.mapa_button);
        botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double lat = ticketsCursor.getDouble(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Latitud));
                Double lon = ticketsCursor.getDouble(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Longitud));
                String idTicket = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_NumTicket));
                String dependencia = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Dependencia));
                String grupo = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Grupo));
                String etapa = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Etapa));
                int rowid = ticketsCursor.getInt(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_ROWID));
                String comentariosSI = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_ComentarioSI));
                String comentariosOp = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_ComentariosOp));

                String tramo = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Tramo));
                String sincronizado = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Sincronizado));
                String prioridadSI = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_PrioridadSI));
                String procede = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Procede));

                String dirGral = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_DirGral));
                String dirArea = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_DirArea));
                String grupoServ = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_GrupoServicios));
                String servi = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_Servicio));
                String limgsi = ticketsCursor.getString(ticketsCursor
                        .getColumnIndex(TicketTable.KEY_lImgSI));

                //ticketsCursor.close();
                  //Todo carga de info para detalle 2
//                Intent i = new Intent(getApplicationContext(),
//                        MapaActivity.class);
                Intent i = new Intent(getApplicationContext(),
                        Detalle2Info.class);
                // sending data to new activity
                //
                Bundle ticketParams = new Bundle();
                ticketParams.putDouble(TicketTable.KEY_Latitud, lat);
                ticketParams.putDouble(TicketTable.KEY_Longitud, lon);
                ticketParams.putString(TicketTable.KEY_NumTicket, idTicket);
                ticketParams.putString(TicketTable.KEY_Grupo, grupo);
                ticketParams.putString(TicketTable.KEY_Dependencia,dependencia);
                ticketParams.putString(TicketTable.KEY_Etapa, etapa);
                ticketParams.putInt(TicketTable.KEY_ROWID, rowid);
                ticketParams.putString(TicketTable.KEY_ComentarioSI,comentariosSI);
                ticketParams.putString(TicketTable.KEY_ComentariosOp,comentariosOp);
                ticketParams.putString(TicketTable.KEY_Tramo, tramo);
                ticketParams.putString(TicketTable.KEY_Sincronizado, sincronizado);

                //valores para verificacion inicial
                ticketParams.putString(TicketTable.KEY_PrioridadSI, prioridadSI);
                ticketParams.putString(TicketTable.KEY_Procede, procede);

                // Activity ConclusionDetail2
                ticketParams.putString(TicketTable.KEY_DirGral, dirGral);
                ticketParams.putString(TicketTable.KEY_DirArea, dirArea);
                ticketParams.putString(TicketTable.KEY_GrupoServicios,
                        grupoServ);
                ticketParams.putString(TicketTable.KEY_Servicio, servi);
                ticketParams.putString(TicketTable.KEY_lImgSI, limgsi);





                i.putExtras(ticketParams);

                startActivity(i);

            }
        });
        new LoadInfo().execute();

        botonAtras = (Button) findViewById(R.id.backbutton_detail_ticket);
        botonAtras.setOnClickListener(Detalle1Info.this);
    }

    @Override
    public void onClick(View v) {
        this.onBackPressed();
    }

    private class LoadInfo extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            ContentResolver cresolver = Detalle1Info.this.getContentResolver();
			/*
			 * OPERACION DE VALIDACION DE TICKETS DESCARGADOS
			 */
            Uri uri = ContentUris.withAppendedId(TicketTable.CONTENT_URI, Long.valueOf(currentIdTicket));
            String[] projection = {
                    TicketTable.KEY_Tramo, TicketTable.KEY_Calle,
                    TicketTable.KEY_Colonia, TicketTable.KEY_Delegacion,
                    TicketTable.KEY_CP, TicketTable.KEY_Sentido,
                    TicketTable.KEY_LugarFisico, TicketTable.KEY_Grupo,
                    TicketTable.KEY_Localizado, TicketTable.KEY_Latitud,
                    TicketTable.KEY_Dependencia, TicketTable.KEY_Etapa,
                    TicketTable.KEY_Longitud, TicketTable.KEY_ROWID,
                    TicketTable.KEY_NumTicket,
                    TicketTable.KEY_ComentarioSI,
                    TicketTable.KEY_ComentariosOp,
                    TicketTable.KEY_Vialidad,
                    TicketTable.KEY_EntreCalle1,
                    TicketTable.KEY_EntreCalle2,
                    TicketTable.KEY_PuntoReferencia,
                    TicketTable.KEY_Motivo,TicketTable.KEY_Descripcion,
                    TicketTable.KEY_ImgIng1,TicketTable.KEY_ImgIng1,
                    TicketTable.KEY_ImgIng2,TicketTable.KEY_ImgIng2,
                    TicketTable.KEY_ImgIng3,TicketTable.KEY_ImgIng3,
                    TicketTable.KEY_Sincronizado,TicketTable.KEY_PrioridadSI,
                    TicketTable.KEY_Procede,

                    TicketTable.KEY_DirGral,
                    TicketTable.KEY_DirArea,
                    TicketTable.KEY_GrupoServicios,
                    TicketTable.KEY_Servicio,
                    TicketTable.KEY_ComentarioSI,
                    TicketTable.KEY_lImgSI,
            };

            ticketsCursor = cresolver.query(uri,projection,null,null,null);

            //abre el ticket
            ContentValues cv = new ContentValues();
            cv.put(TicketTable.KEY_EstadoTicket, TicketMetaData.CONST_TICKET_ABIERTO);
            cresolver.update(uri, cv, null, null);

            return ticketsCursor.getColumnCount();
        }

        protected void onPostExecute(final Integer respuesta) {
            super.onPostExecute(respuesta);
            // Actuliza la interfaz
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ticketsCursor.moveToFirst()) {
                        // SE MUESTRA LA INFORMACION DE LA BASE DE DATOS EN LA PANTALLA
                        imgIng1 = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_ImgIng1));
                        imgIng2 = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_ImgIng2));
                        imgIng3 = ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_ImgIng3));
                        int loader = R.drawable.fotodefault;
                        img1 = (ImageView) findViewById(R.id.img1_COImageView);
                        img2 = (ImageView) findViewById(R.id.img2_COImageView);
                        img3 = (ImageView) findViewById(R.id.img3_COImageView);
                        ImageLoader imgLoader = new ImageLoader(getApplicationContext());

                        imgLoader.DisplayImage(imgIng1, loader, img1);
                        if(imgIng1.length() > 0){
                            Log.d("ADX2099" , "Que trae la imagen " + imgIng1);
                            img1.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    urlImage = imgIng1;

                                    Intent i = new Intent(getApplicationContext(),
                                            ImagePreviewActivity.class);
                                    i.putExtra("url",urlImage);
                                    startActivity(i);

                                }
                            });
                        }
                        imgLoader.DisplayImage(imgIng2, loader, img2);
                        if(imgIng2.length()>0){
                            System.out.println("que trae el ImgIng " + imgIng2);
                            img2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    urlImage = imgIng2;
                                    Intent i = new Intent(getApplicationContext(),
                                            ImagePreviewActivity.class);
                                    i.putExtra("url",urlImage);
                                    startActivity(i);

                                }
                            });
                        }
                        imgLoader.DisplayImage(imgIng3, loader, img3);
                        if(imgIng3.length()>0){
                            System.out.println("que trae el ImgIng " + imgIng3);
                            img3.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    urlImage = imgIng3;
                                    Intent i = new Intent(getApplicationContext(),
                                            ImagePreviewActivity.class);
                                    i.putExtra("url",urlImage);
                                    startActivity(i);

                                }
                            });
                        }

                        motivoText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Motivo)));
                        descripcionText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Descripcion)));
                        vialidadText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Vialidad)));
                        tramoText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Tramo)));
                        calleText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Calle)));
                        coloniaText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Colonia)));
                        delegacionText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Delegacion)));
                        cpText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_CP)));
                        sentidoText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Sentido)));
                        lugarFisicoText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_LugarFisico)));
                        localizadoText.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_Localizado)));
                        entreCalle1.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_EntreCalle1)));
                        entreCalle2.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_EntreCalle2)));
                        puntoDeReferencia.setText(ticketsCursor.getString(ticketsCursor.getColumnIndex(TicketTable.KEY_PuntoReferencia)));
                    }

                    replaceNULLwithEmpty();

                    Log.d(TAG,"Imagen 1 "+ ticketsCursor.getString(ticketsCursor
                            .getColumnIndex(TicketTable.KEY_ImgIng1)));
                    Log.d(TAG,"Imagen 2 "+ ticketsCursor.getString(ticketsCursor
                            .getColumnIndex(TicketTable.KEY_ImgIng2)));
                    Log.d(TAG,"Imagen 3"+ ticketsCursor.getString(ticketsCursor
                            .getColumnIndex(TicketTable.KEY_ImgIng3)));
                }
            });
        }
    }
    // Función que reemplaza los valores que dicen null por una cadena vacia
    private void replaceNULLwithEmpty() {
        if (motivoText.getText().equals("null"))
            motivoText.setText(" ");

        if (descripcionText.getText().equals("null"))
            descripcionText.setText(" ");

        if (coloniaText.getText().equals("null"))
            coloniaText.setText(" ");

        if (vialidadText.getText().equals("null"))
            vialidadText.setText(" ");

        if (tramoText.getText().equals("null"))
            tramoText.setText(" ");

        if (calleText.getText().equals("null"))
            calleText.setText(" ");

        if (delegacionText.getText().equals("null"))
            delegacionText.setText(" ");

        if (cpText.getText().equals("null"))
            cpText.setText(" ");

        if (sentidoText.getText().equals("null"))
            sentidoText.setText(" ");

        if (lugarFisicoText.getText().equals("null"))
            lugarFisicoText.setText(" ");

        if (localizadoText.getText().equals("null"))
            localizadoText.setText(" ");

        if (entreCalle1.getText().equals("null"))
            entreCalle1.setText(" ");

        if (entreCalle2.getText().equals("null"))
            entreCalle2.setText(" ");

        if (puntoDeReferencia.getText().equals("null"))
            puntoDeReferencia.setText(" ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle1_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}