package com.agu.operaciones;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.ImageCache.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Detalle2Info extends ActionBarActivity implements
        View.OnClickListener {

    private TextView titulo, direccionGralText, direccionAreaText,
            grupoServicioText, servicioText, comentarioSiText;

    private ImageView[] img = new ImageView[3];

    private Button btnSiguiente, atrasButton;
    private Double lat, lon, latitudProv,longitudProv;
    // private Double lat=19.367571;
    // private Double lon=-99.161933 ;
    private String currentNumTicket;
    private String[] urlImage = new String[3];
    private final String TAG = "ConclusionDetalle2Info";

    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        setContentView(R.layout.activity_detalle2_info);
        b = this.getIntent().getExtras();
        currentNumTicket = b.getString(TicketMetaData.TicketTable.KEY_NumTicket);

        initUI();
    }
    private void initUI() {
        titulo = (TextView) findViewById(R.id.tituloNumTicket2);
        titulo.setText("Ticket: " + currentNumTicket);

        direccionGralText = (TextView) findViewById(R.id.dirGenC_textView);
        direccionAreaText = (TextView) findViewById(R.id.dirAreC_textView);
        grupoServicioText = (TextView) findViewById(R.id.grupoServicioC_textView);
        servicioText = (TextView) findViewById(R.id.servicioC_textView);
        comentarioSiText = (TextView) findViewById(R.id.comentarioSIC_textView);
        btnSiguiente = (Button) findViewById(R.id.siguienteC_button);

        atrasButton = (Button) findViewById(R.id.backbutton_conclusion2);
        atrasButton.setOnClickListener(this);

        direccionGralText.setText(b.getString(TicketTable.KEY_DirGral));
        direccionAreaText.setText(b.getString(TicketTable.KEY_DirArea));
        grupoServicioText.setText(b
                .getString(TicketTable.KEY_GrupoServicios));
        servicioText.setText(b.getString(TicketTable.KEY_Servicio));
        comentarioSiText.setText(b.getString(TicketTable.KEY_ComentarioSI));

        int loader = R.drawable.fotodefault;
        img[0] = (ImageView) findViewById(R.id.img1_ImageView);
        img[1] = (ImageView) findViewById(R.id.img2_ImageView);
        img[2] = (ImageView) findViewById(R.id.img3_ImageView);
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        latitudProv = b.getDouble(TicketTable.KEY_Latitud);
        longitudProv = b.getDouble(TicketTable.KEY_Longitud);
        //Log.i(TAG, "lat es!!!!! "+ latitudProv.toString() );

        try {

            JSONArray jsonArrayImg = new JSONArray(
                    b.getString(TicketTable.KEY_lImgSI));
            if (jsonArrayImg.length() > 0) {
                for (int i = 0; i < jsonArrayImg.length(); i++) {

                    JSONObject jsonObject = jsonArrayImg.getJSONObject(i);
                    urlImage[i] = jsonObject
                            .getString(TicketTable.KEY_ImgSI);
                    System.out.println("que trae el ImgSI " + urlImage[i]);

                    //imgLoader.DisplayImage("http://189.254.14.169/Imagen/AGENCIA%20DE%20GESTI%C3%93N%20URBANA/Supervisi%C3%B3n%20inicial/si012014-17039-1.jpg", loader, img[i]);
                    urlImage[i]=remove1(urlImage[i]);
                    System.out.println("que trae el ImgSI Convertida " + urlImage[i]);
                    imgLoader.DisplayImage(urlImage[i], loader, img[i]);

                    Log.i(TAG, "que traen el json Latitud " + jsonObject.getString(TicketTable.KEY_LatitudInicial));
                    if (jsonObject.getString(TicketTable.KEY_LatitudInicial)
                            .equals("null")
                            || jsonObject.getString(
                            TicketTable.KEY_LongitudInicial).equals(
                            "null")) {
                        lat = latitudProv;//0.0;
                        lon = longitudProv;
                        //0.0;
                        // lat = 19.367571;
                        // lon = -99.161933;
                    } else {

                        lat = jsonObject
                                .getDouble(TicketTable.KEY_LatitudInicial);
                        lon = jsonObject
                                .getDouble(TicketTable.KEY_LongitudInicial);
                    }
                    img[0].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(urlImage[0].length()>0){
                                Intent i = new Intent(getApplicationContext(),
                                        ImagePreviewActivity.class);
                                i.putExtra("url", urlImage[0]);
                                startActivity(i);
                            }
                        }
                    });
                    img[1].setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(urlImage[1].length()>0){
                                Intent i = new Intent(getApplicationContext(),
                                        ImagePreviewActivity.class);
                                i.putExtra("url", urlImage[1]);
                                startActivity(i);
                            }
                        }
                    });
                    img[2].setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(urlImage[2].length()>0){
                                Intent i = new Intent(getApplicationContext(),
                                        ImagePreviewActivity.class);
                                i.putExtra("url", urlImage[2]);
                                startActivity(i);
                            }
                        }
                    });
                }

            } else {
                lat = 0.0;
                lon = 0.0;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("error con jsonObject", e.getMessage());

        }

        //TicketTable.KEY_Latitud

        replaceNULLwithEmpty();

        btnSiguiente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(),
                        MapaActivity.class);
                // sending data to new activity
                //
                if (lat == 0.0 || lon == 0.0) {
                    AlertDialog aD = new AlertDialog.Builder(
                            Detalle2Info.this).create();
                    aD.setTitle("Atención");

                    // Setting Dialog Message
                    aD.setMessage("No se cuenta con imagenes de supervisión inicial, comunicate con tu administrativo.");
                    aD.setButton("Aceptar",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            });
                    aD.show();
                } else {

                    b.putDouble(TicketTable.KEY_LatitudInicial, lat);
                    b.putDouble(TicketTable.KEY_LongitudInicial, lon);
                    i.putExtras(b);
                    startActivity(i);
                }

            }
        });

    }

    private void replaceNULLwithEmpty() {
        if (direccionGralText.getText().equals("null"))
            direccionGralText.setText(" ");
        if (direccionAreaText.getText().equals("null"))
            direccionAreaText.setText(" ");
        if (grupoServicioText.getText().equals("null"))
            grupoServicioText.setText(" ");
        if (servicioText.getText().equals("null"))
            servicioText.setText(" ");
        if (comentarioSiText.getText().equals("null"))
            comentarioSiText.setText(" ");
    }
    public static String remove1(String input) {
        // Cadena de caracteres original a sustituir.
        //String original = "áéíóúÁÉÍÓÚ";
        String[] original = {"ó","Ó"};
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String[] caracteres = {"%C3%B3","%C3%93"};
        String output = input;
        for (int i=0; i<original.length; i++) {
            // Reemplazamos los caracteres especiales.
            //output = output.replace(original.charAt(i), ascii.charAt(i));
            output = output.replaceAll(original[i], caracteres[i]);

        }//for i
        return output;
    }




    @Override
    public void onClick(View v) {
        this.onBackPressed();
    }
}