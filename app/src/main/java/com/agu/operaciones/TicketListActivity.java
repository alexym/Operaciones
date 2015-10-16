package com.agu.operaciones;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agu.operaciones.adapters.TicketListAdapter;
import com.agu.operaciones.asynctasks.DownloadTickets;
import com.agu.operaciones.asynctasks.SincronizaTemps;
import com.agu.operaciones.asynctasks.SincronizaTicket;
import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;
import com.agu.operaciones.utilities.FileCameraManager;
import com.agu.operaciones.utilities.NoInternetException;
import com.agu.operaciones.utilities.SessionManager;
import com.agu.operaciones.utilities.SupervisorDialog;
import com.agu.operaciones.webservices.DataWebservice;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TicketListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Etiqueta para Log de la activity
    String TAG = "TicketListActivity";

    String nuevoNumTicket;

    //Elementos de interfaz
    TicketListAdapter ticketListAdapter;
    ListView listaTickets;
    CursorLoader cursorLoader;
    private File directory;
    private FileCameraManager fileCamera;

    private ArrayList<String> ticketListActualizar = new ArrayList<String>();
    private ArrayList<String> ticketListSincronizar = new ArrayList<String>();

    ListView lVTicketsActualize,lVTicketsSincronize;
    Dialog dialogActualizar,dialogSincronizar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        initUI();
        Bundle extras = this.getIntent().getExtras();
        //Evalua si descarga los tickets o solo los muestra
        if (extras != null && extras.getBoolean(TicketMetaData.DESCARGA_TICKETS_INICIO)) {
            new DownloadTickets(this, "LoadTickets Thread").execute("");
        } else {
            getLoaderManager().initLoader(1, null, this);

        }
        this.setTitle("Operaciones");

        //LLAMAMOS AL METODO QUE CREA EL INTENT
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initUI()
    {
        setContentView(R.layout.activity_ticket_list);
        listaTickets = (ListView)findViewById(R.id.listaTickets);
        ticketListAdapter = new TicketListAdapter(getApplicationContext(), null);
        listaTickets.setAdapter(ticketListAdapter);

        listaTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(0xFFE6E7E8);

                TextView idTicketTV = (TextView)view.findViewById(R.id.id_TV);
                TextView EtapaTV = (TextView)view.findViewById(R.id.etapaTV);
                TextView numTicket = (TextView) view.findViewById(R.id.numTicketTV);

                String etapaRow = EtapaTV.getText().toString();
                String rowId = idTicketTV.getText().toString();
                String product = numTicket.getText().toString();

                if (etapaRow.equals(TicketMetaData.CONST_ETAPA_OPERACION)) {
                    Intent detail = new Intent(getApplicationContext(),
                            Detalle1Info.class);
                    detail.putExtra(TicketTable.KEY_EtapaSupervision, true);
                    detail.putExtra(TicketTable.KEY_NumTicket, product);
                    detail.putExtra(TicketTable.KEY_IdTicket, rowId);


                    startActivity(detail);

                }


            }
        });

         /*
                ELEMENTOS DEL DIALOG CUSTOM
                 */
        dialogActualizar = new Dialog(TicketListActivity.this);
        dialogActualizar.setContentView(R.layout.dialog_actualizar_ticket);
        dialogActualizar.setTitle("Actualización de Ticket");
        lVTicketsActualize = (ListView) dialogActualizar.findViewById(R.id.listView_tickets);
        lVTicketsActualize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                Object o = parent.getItemAtPosition(position);
                String numTicketText = o.toString();
                dialogActualizar.dismiss();
                new DownloadActualizacionTicket(numTicketText,TicketListActivity.this).execute("");

            }
        });


        dialogSincronizar = new Dialog(TicketListActivity.this);
        dialogSincronizar.setContentView(R.layout.dialog_sincronizar_ticket);
        dialogSincronizar.setTitle("Sincronización de Ticket");
        lVTicketsSincronize = (ListView) dialogSincronizar.findViewById(R.id.listView_tickets_sincronizar);
        lVTicketsSincronize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Object o = parent.getItemAtPosition(position);
                String numTicketText = o.toString();
                //Log.d(TAG + numTicketText);

                if (numTicketText.contains("t_") == true) {
                    Log.d("ADX2099", "SI CONTIENE TTT");
                    new SincronizaTemps(numTicketText, TicketListActivity.this).execute("");
                } else {
                    Log.d("ADX2099", "NO CONTIENE TTTT");
                    new SincronizaTicket(numTicketText, TicketListActivity.this).execute("");
                }
                dialogSincronizar.dismiss();
            }
        });

    }

    private void handleIntent(Intent intent){
        String query = "";
        if(Intent.ACTION_VIEW.equals(intent.getAction())){
            Intent ticketIntent = new Intent(this, Detalle1Info.class);
            ticketIntent.setData(intent.getData());
            Uri uri = intent.getData();
            Long id_uri = ContentUris.parseId(uri);
            Uri uriBase = ContentUris.withAppendedId(TicketTable.CONTENT_URI, id_uri);
            ContentResolver cresolverSearch = this.getContentResolver();
            String[] projection = {TicketTable.KEY_Etapa,TicketTable.KEY_NumTicket};
            Cursor cursor = cresolverSearch.query(uriBase,projection,null,null,null);
            String tipoEtapa ="";
            String numTicketSearch = "";
            if (cursor == null) {
                //finish();
                Log.i(TAG,"ni mergas");
            } else{
                cursor.moveToFirst();
                tipoEtapa = cursor.getString(0);
                numTicketSearch = cursor.getString(1);
            }
            if (tipoEtapa.equals(TicketMetaData.CONST_ETAPA_OPERACION)) {
                Intent detail = new Intent(getApplicationContext(),
                        Detalle1Info.class);
                detail.putExtra(TicketTable.KEY_EtapaSupervision, true);
                detail.putExtra(TicketTable.KEY_NumTicket, numTicketSearch);
                detail.putExtra(TicketTable.KEY_IdTicket, id_uri.toString());
                startActivity(detail);
            }
        }else if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            //Metodo que gestiona el boton buscar del teclado, de momento no es funcional

            //Manejan la busqueda
            query = intent.getStringExtra(SearchManager.QUERY);
            //Log.i(TAG,"Que trae en SEARCH" + query );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticket_list, menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(TicketListActivity.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    public void onSupervisorRequested(){
        Log.d(TAG, "PIDO EL SUPERVISOR");
        SessionManager sm = new SessionManager(TicketListActivity.this);
        HashMap<String, String> hm = sm.getDetallesSession();
        String nombre = hm.get(SessionManager.NOMBRE);
        String idTelefono = hm.get(SessionManager.IDTEL);
        String horaInicio = hm.get(SessionManager.HORAINICIO);
        String cuadrilla = hm.get(SessionManager.CUADRILLA);
        new SupervisorDialog(nombre, idTelefono, horaInicio, cuadrilla).show(getFragmentManager(), "Supervisor");
    }

    public void onActualizarRequested(){
        ticketListActualizar.clear();
        ContentResolver cresolver = TicketListActivity.this.getContentResolver();
        String[] projection = new String[] {TicketTable.KEY_NumTicket,TicketTable.KEY_Sincronizado };
        Cursor cursor = cresolver.query(TicketTable.CONTENT_URI,projection,null,null,null);
        while (cursor.moveToNext()){
            if ( !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Sincronizado)).equals(TicketMetaData.CONST_SINCRONIZADO_SI )
                    && !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)).contains("t_") ) {

                ticketListActualizar.add(cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)));

            }

            if( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Sincronizado)).equals(TicketMetaData.CONST_SINCRONIZADO_NO )){
                ticketListActualizar.add(cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TicketListActivity.this,android.R.layout.simple_expandable_list_item_1,ticketListActualizar);
        lVTicketsActualize.setAdapter(arrayAdapter);
        dialogActualizar.show();
    }

    public void onSalirRequested(){

        Log.d(TAG, "SALIR");
        new LogoutCallService(TicketListActivity.this).execute("");
    }

    public void onSincronizarRequested(){
        ticketListSincronizar.clear();
        ContentResolver cresolver = TicketListActivity.this.getContentResolver();
        String[] projection = new String[] {TicketTable.KEY_NumTicket,TicketTable.KEY_Sincronizado };
        Cursor cursor = cresolver.query(TicketTable.CONTENT_URI,projection,null,null,null);
        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Sincronizado)).equals(TicketMetaData.CONST_SINCRONIZADO_SI) || cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Sincronizado)).equals(TicketMetaData.CONST_SINCRONIZADO_NO )){
                ticketListSincronizar.add(cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)));
            }
        }
        ArrayAdapter<String> arrayAdapterSinc = new ArrayAdapter<String>(TicketListActivity.this,android.R.layout.simple_expandable_list_item_1,ticketListSincronizar);
        lVTicketsSincronize.setAdapter(arrayAdapterSinc);
        dialogSincronizar.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                //onSearchRequested();
                return true;
            case R.id.menu_supervisor:
                onSupervisorRequested();
                return true;
            case R.id.menu_actualizar:
                onActualizarRequested();
                return true;
            case R.id.menu_sincronizar:
                onSincronizarRequested();
                return true;
            case R.id.menu_salir:
                onSalirRequested();
                return true;


            default:
                return false;
        }

        //return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {TicketTable.KEY_ROWID,TicketTable.KEY_NumTicket,
                TicketTable.KEY_Etapa,
                TicketTable.KEY_Motivo,
                TicketTable.KEY_Sincronizado,
                TicketTable.KEY_Vialidad,
                TicketTable.KEY_Colonia,
                TicketTable.KEY_Tramo,
                TicketTable.KEY_Calle,
                TicketTable.KEY_Etapa,
                TicketTable.KEY_EstadoTicket,
        };

        cursorLoader = new CursorLoader(this, TicketTable.CONTENT_URI, projection, null, null, null);

        return cursorLoader;
    }
//
//    @Override
//    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
//
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if(cursor != null && ticketListAdapter != null)
            ticketListAdapter.swapCursor(cursor);
        else
            Log.v(TAG, "Load finished, mAdapter is NULL");

        if(cursorLoader.getId() == 1){
            ticketListAdapter.changeCursor(cursor);
        }

    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if(ticketListAdapter != null)
            ticketListAdapter.swapCursor(null);
        else
            Log.v("TicketsListaActivity","Load finished, mAdapter is NULL");
    }

    private class LogoutCallService extends AsyncTask<Object, Object, Integer> {
        private final int RESPUESTA_EXITOSA = 0;
        private final int RESPUESTA_ERROR = 1;

        Cursor ticketCursor;

        ProgressDialog pd = null;
        SessionManager sm;
        Activity _context;
        String mensajeError = "";
        int respuesta = RESPUESTA_EXITOSA;


        private LogoutCallService(Activity _context) {
            this._context = _context;
            pd = null;
            sm = new SessionManager(this._context);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pd = ProgressDialog.show(_context, "Aviso", "Cerrando sesión ...",true);

        }

        @Override
        protected Integer doInBackground(Object... params) {

            int noSincronizados = getTotalTicketsNoSincronizados();

            if (noSincronizados == 0) {

                List<NameValuePair> reporte = new ArrayList<NameValuePair>();
                HashMap<String, String> valoresSesion = sm.getDetallesSession();
                //reporte.add(new BasicNameValuePair(SessionManager.IDSUP,
                reporte.add(new BasicNameValuePair("IdOp",
                        valoresSesion.get("IdOp")));

                try {
                    JSONObject logoutData = DataWebservice.callService(DataWebservice.LOGOUT, reporte, "GET", _context);
                    Log.i(TAG,logoutData.toString());
                    if (logoutData.getString("Error").equals("true")) {
                        respuesta = RESPUESTA_ERROR;
                        mensajeError = logoutData.getString("ErrorMsg");
                    } else {
                        ContentResolver cr = TicketListActivity.this.getContentResolver();
                        cr.delete(TicketTable.CONTENT_URI,null,null);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG,"TicketsActivity.LogoutCallService->doInBackground Error al parsear JSON " + e.getMessage());
                    respuesta = RESPUESTA_ERROR;
                    e.printStackTrace();
                } catch (NoInternetException nie) {
                    // TODO Auto-generated catch block
                    nie.printStackTrace();
                    respuesta = RESPUESTA_ERROR;
                    mensajeError = nie.getMessage();
                } finally {
                    //adapter.close();
                }

            } else {
                respuesta = RESPUESTA_ERROR;
                mensajeError = "Aún hay tickets por sincronizar";
            }

            return respuesta;
        }


        @Override
        protected void onPostExecute(Integer result){
            pd.cancel();
            if (result.intValue() == RESPUESTA_EXITOSA) {
                fileCamera = new FileCameraManager(TicketListActivity.this, null,FileCameraManager.path_general);
                directory = new File(FileCameraManager.path);
                // Se envia la ruta de la carpeta a borrar con todo y contenido
                FileCameraManager.DeleteRecursive(directory);
                // Se elimina el sharedPreferences con las referencias de la
                // latitud y longitud de cada imagen
                SharedPreferences dataImageTicket = getSharedPreferences(TicketMetaData.SP_TICKET, MODE_PRIVATE);
                dataImageTicket.edit().clear().commit();
                sm.cerrarSesion();
                finish();

            } else {
                Toast.makeText(_context, mensajeError, Toast.LENGTH_SHORT)
                        .show();
            }

        }


        public int getTotalTicketsNoSincronizados(){
            int total = 0;
            ContentResolver cr = TicketListActivity.this.getContentResolver();
            String[] projection = {TicketTable.KEY_ROWID, TicketTable.KEY_IdTicket,
                    TicketTable.KEY_NumTicket, TicketTable.KEY_Etapa,
                    TicketTable.KEY_Sincronizado};

            ticketCursor = cr.query(TicketTable.CONTENT_URI,projection,TicketTable.KEY_Sincronizado+" = '" + TicketMetaData.CONST_SINCRONIZADO_NO+"'" ,null,null);
            int idTicketIndex = ticketCursor.getColumnIndex(TicketTable.KEY_IdTicket);
            int idTicketNum = ticketCursor.getColumnIndex(TicketTable.KEY_NumTicket);
            int idTicketSinc = ticketCursor.getColumnIndex(TicketTable.KEY_Sincronizado);

            if(ticketCursor.moveToFirst()){
                do{
                    Log.i(TAG +" id cursor","es "+ticketCursor.getString(idTicketIndex));
                    Log.i(TAG+" num Tickets","es "+ticketCursor.getString(idTicketNum));
                    Log.i(TAG+" estado de sinc","es "+ticketCursor.getString(idTicketSinc));
                    total = ticketCursor.getInt(0);

                }while(ticketCursor.moveToNext());

                ticketCursor.close();
            }
            return total;
        }

    }


      /*
	     * Clase que descarga las correcciones a tickets previamente descargados e
	     * inserta los que sean nuevos
	     */

    private class DownloadActualizacionTicket extends AsyncTask<String, Integer, Integer> {

        private final int RESPUESTA_EXITOSA = 0;
        private final int RESPUESTA_ERROR = 1;
        private String mensajeError = "";
        private String numTicket;// P�rametro que representa numero de ticket a
        // actualizar

        Activity _contexto;
        ProgressDialog pd;

        public DownloadActualizacionTicket(String ticket, Activity _context) {
            numTicket = ticket;
            _contexto = _context;
            pd = null;
        }

        @Override
        protected void onPreExecute() {
            // Corre en el UI thread
            super.onPreExecute();
            pd = ProgressDialog.show(_contexto, "Aviso", "Actualizando ticket " + numTicket + " ...", true);
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            // Se har�a la descarga de tickets
            JSONObject descargados;
            Integer respuesta = RESPUESTA_EXITOSA;

            List<NameValuePair> ticketParams = new ArrayList<NameValuePair>();

            SessionManager sm = new SessionManager(TicketListActivity.this);
            HashMap<String, String> hm = sm.getDetallesSession();
            String usuarioSup = hm.get(SessionManager.USUARIO);
            ticketParams.add(new BasicNameValuePair("UsuarioSup", usuarioSup));
            ticketParams.add(new BasicNameValuePair("Ticket", numTicket));

            // *** OPERACION DE CONSULTA AL SERVICIO ***
            String respuestaError = null;

            try {
                JSONObject ticketData;

                ticketData = DataWebservice.callService(DataWebservice.SINCRONIZAR,ticketParams,DataWebservice.METHOD_GET,_contexto);
                respuestaError = ticketData.getString("Error");

                if (respuestaError.equals("true")) {
                    respuesta = RESPUESTA_ERROR;
                    mensajeError = ticketData.getString("ErrorMsg");
                } else {


                    // *** OPERACION DE ACTUALIZACI�N EN BASE DE DATOS ***
                    JSONArray ticketArray = ticketData.getJSONArray("lTickets");
                    JSONObject ticket = (JSONObject) ticketArray.get(0);
                    // construyendo el content values
                    ContentValues valores = new ContentValues();
                    Iterator it = ticket.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if (ticket.getString(key) == null) {
                            valores.putNull(key);
                        } else {
                            valores.put(key, ticket.getString(key));
                        }
                    }


                    ContentResolver cr = TicketListActivity.this.getContentResolver();
                    cr.update(TicketMetaData.TicketTable.CONTENT_URI,valores,TicketTable.KEY_NumTicket + " = ?",new String[] { ticket.getString(TicketTable.KEY_NumTicket) });

                }

            } catch (JSONException ex) {
                ex.printStackTrace();
                Log.e(TAG,ex.getMessage());
                respuesta = RESPUESTA_ERROR;
                mensajeError = ex.getMessage();
            } catch (SQLException sqle) {
                Log.e(TAG,"Error al tratar de actualizar el ticket " + numTicket);
                respuesta = RESPUESTA_ERROR;
                mensajeError = sqle.getMessage();
            } catch (NoInternetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mensajeError = e.getMessage();
                respuesta = RESPUESTA_ERROR;
            }

            return respuesta;
        }

        protected void onPostExecute(final Integer respuesta) {
            pd.dismiss();

            if (respuesta == RESPUESTA_EXITOSA) {
                mensajeError = "Ticket " + numTicket + " actualizado correctamente";
            }
            Toast.makeText(_contexto, mensajeError, Toast.LENGTH_SHORT).show();
        }

    }


}

