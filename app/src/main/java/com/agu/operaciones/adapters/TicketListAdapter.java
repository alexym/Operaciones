package com.agu.operaciones.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.agu.operaciones.R;
import com.agu.operaciones.providers.TicketMetaData;
import com.agu.operaciones.providers.TicketMetaData.TicketTable;

/**
 * Created by Cloudco on 14/10/15.
 */
public class TicketListAdapter extends SimpleCursorAdapter {
    Context cxt;
    String TAG = "TicketlistAdapter";



    static String[] from = {TicketTable.KEY_ROWID,TicketTable.KEY_NumTicket,
            TicketTable.KEY_Etapa,
            TicketTable.KEY_Motivo};
    static int[] to = {R.id.id_TV,R.id.numTicketTV,
            R.id.etapaTV,
            R.id.motivoTV};

    public TicketListAdapter(Context context, Cursor c) {
        super(context, R.layout.list_item, c, from, to, 0);
        cxt = context;


    }

    @Override
    public View getView(int pos, View inView, ViewGroup parent) {
        View v = super.getView(pos, inView, parent);


        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
            //Log.i(TAG,"es nulo");
        }else{
            //Log.i(TAG,"no es nulo");
        }

        Cursor cursor = getCursor();

        cursor.moveToPosition(pos);


        String sincronizado = cursor.getString(cursor
                .getColumnIndex(TicketTable.KEY_Sincronizado));
        //Log.i(TAG,"la pos es "+ sincronizado);

        ImageView imageIco = (ImageView) v.findViewById(R.id.sincIV);
        LinearLayout ly = (LinearLayout) v.findViewById(R.id.linearLayout_itemTicket);
        if ( sincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_SI) ) {
            imageIco.setImageDrawable(cxt.getResources().getDrawable(R.drawable.icoverde));
        } else if( sincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_NO) ){
            imageIco.setImageDrawable(cxt.getResources().getDrawable(R.drawable.icorojo));
        }else if( sincronizado.equals(TicketMetaData.CONST_SINCRONIZADO_AUN_NO) ){
            imageIco.setImageDrawable(cxt.getResources().getDrawable(R.drawable.icoamarillo));
        }
        TextView vialidadTV = (TextView) v.findViewById(R.id.datosVialesTV);


        StringBuffer datosV = new StringBuffer();
        if( !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Vialidad)).equals("null"))
            datosV.append( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Vialidad)) + "\n" );

        if( !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Colonia)).equals("null"))
            datosV.append( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Colonia)) + "\n" );

        if( !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Tramo)).equals("null"))
            datosV.append( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Tramo)) + "\n" );

        if( !cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Calle)).equals("null"))
            datosV.append( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Calle)) + "\n");

        if( cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Etapa)).equals("Notificación")){
            //datosV.append(cursor.getString(cursor.getColumnIndex(TicketsAdapter.KEY_Etapa)));
            String myEtapa = cursor.getString(cursor.getColumnIndex(TicketTable.KEY_Etapa));
            //Log.d("ADX2099", "QUE VALOR TIENES::" + myEtapa);
            String subs = "Supervisión", regex = "Notificación";
            String rep =  "Supervisión";
            //myEtapa.replaceAll(regex, subs);

            myEtapa.replace(myEtapa, rep);

        }
        vialidadTV.setText(datosV);
        if(cursor.getInt(cursor.getColumnIndex(TicketTable.KEY_EstadoTicket))== TicketMetaData.CONST_TICKET_ABIERTO){
            //System.out.println("tickets abierto "+cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)));
            ly.setBackgroundColor(cxt.getResources().getColor(R.color.oscuro_row));
        }else if(cursor.getInt(cursor.getColumnIndex(TicketTable.KEY_EstadoTicket))==TicketMetaData.CONST_TICKET_CERRADO){
            //System.out.println("ticket cerrado "+cursor.getString(cursor.getColumnIndex(TicketTable.KEY_NumTicket)));
            ly.setBackgroundColor(cxt.getResources().getColor(R.color.white));
        }
        TextView id_TV = (TextView) v.findViewById(R.id.id_TV);
        id_TV.setVisibility(TextView.GONE);

        return v;
    }

}

