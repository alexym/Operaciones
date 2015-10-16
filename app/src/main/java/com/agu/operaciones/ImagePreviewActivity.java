package com.agu.operaciones;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImagePreviewActivity extends AppCompatActivity implements
        View.OnClickListener {
    ImageView imageDetail;
    Button atrasBtn;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    int loader = R.drawable.fotodefault;
    String urlImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlImg = extras.getString("url");
            Log.i("Image Preview", urlImg);
        }
        this.setTitle("Detalle Imagen");
        imageDetail = (ImageView) findViewById(R.id.imageViewPreviewZoom);
        new DownloadImage(ImagePreviewActivity.this, urlImg).execute("");
        atrasBtn = (Button)findViewById(R.id.backbutton_imagePreview);
        atrasBtn.setOnClickListener(this);
        /**
         * set on touch listner on image
         */
        imageDetail.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:

                        oldDist = spacing(event);

                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;

                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x,
                                    event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x,
                                        midPoint.y);
                            }
                        }
                        break;

                }
                view.setImageMatrix(matrix);

                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float)Math.sqrt(x * x + y * y);

            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });

    }
    private class DownloadImage extends AsyncTask<String, Integer, Integer> {

        Activity contexto;
        ProgressDialog pd = null;
        URL urlImage;
        Bitmap imagenDescargada = null;
        private final int RESPUESTA_EXITOSA = 0;
        private final int RESPUESTA_ERROR = 1;

        public DownloadImage(Activity ctx, String url){
            contexto = ctx;
            pd = null;
            try {
                urlImage = new URL(url);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        protected void onPreExecute() {
            // Corre en el UI thread
            super.onPreExecute();
            pd = ProgressDialog.show(contexto, "Aviso",
                    "Descargando Imagen...", true);
        }
        public Integer getRemoteImage(final URL aURL) {
            try {
                final URLConnection conn = aURL.openConnection();
                conn.connect();
                final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                final Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                imagenDescargada = bm;
                return RESPUESTA_EXITOSA;
            } catch (IOException e) {}
            return RESPUESTA_ERROR;
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            Integer respuesta = RESPUESTA_EXITOSA;

            return getRemoteImage(urlImage);
        }
        protected void onPostExecute(final Integer respuesta) {
            // desecha el dialogo antes de Terminar de cargar los productos
            pd.cancel();

            if (respuesta.intValue() == RESPUESTA_EXITOSA) {

                // Actuliza la interfaz
                runOnUiThread(new Runnable() {
                    public void run() {
                        imageDetail.setImageBitmap(imagenDescargada);
                    }
                });

            } else {
                Toast.makeText(contexto, "Error en la descarga de la imagen, intentelo de nuevo", Toast.LENGTH_SHORT)
                        .show();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_preview, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

}