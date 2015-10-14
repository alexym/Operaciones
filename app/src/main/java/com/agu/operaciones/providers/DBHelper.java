package com.agu.operaciones.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cloudco on 14/10/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = TicketMetaData.DB_NAME;
    private final static int DATABASE_VERSION = TicketMetaData.DB_VERSION;

    /*
     * sentencia sql para creacion de tabla principal
     */
    private static final String  SQL_DATABASE_CREATE =
            " CREATE TABLE IF NOT EXISTS "+TicketMetaData.TABLE_TICKETS+" ("

                    + TicketMetaData.TicketTable.KEY_ROWID + " integer primary key autoincrement, "
                    + TicketMetaData.TicketTable.KEY_NumTicket + " text , "
                    + TicketMetaData.TicketTable.KEY_Materiales + " text default '', "
                    + TicketMetaData.TicketTable.KEY_Motivo + " text, "
                    + TicketMetaData.TicketTable.KEY_LugarFisico + " text default '', "
                    + TicketMetaData.TicketTable.KEY_Latitud + " text, "
                    + TicketMetaData.TicketTable.KEY_Longitud + " text, "
                    + TicketMetaData.TicketTable.KEY_Tramo + " text default '', "
                    + TicketMetaData.TicketTable.KEY_TipoAvenida + " text, "
                    + TicketMetaData.TicketTable.KEY_Servicio + " text, "
                    + TicketMetaData.TicketTable.KEY_Sentido + " text default '', "
                    + TicketMetaData.TicketTable.KEY_PuntoReferencia + " text, "
                    + TicketMetaData.TicketTable.KEY_PrioridadSI + " text, "
                    + TicketMetaData.TicketTable.KEY_Prioridad + " text, "
                    + TicketMetaData.TicketTable.KEY_Procede + " text default 'Si', "
                    + TicketMetaData.TicketTable.KEY_Localizado + " text default '', "
                    + TicketMetaData.TicketTable.KEY_lImgSI + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgOp3 + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgOp2 + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgOp1 + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgIng3 + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgIng2 + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgIng1 + " text, "
                    + TicketMetaData.TicketTable.KEY_IdTicket + " text, "
                    + TicketMetaData.TicketTable.KEY_ImgSF3 + " text,"
                    + TicketMetaData.TicketTable.KEY_ImgSF2 + " text,"
                    + TicketMetaData.TicketTable.KEY_ImgSF1 + " text,"
                    + TicketMetaData.TicketTable.KEY_GrupoServicios + " text, "
                    + TicketMetaData.TicketTable.KEY_Descripcion + " text, "
                    + TicketMetaData.TicketTable.KEY_Grupo + " text, "
                    + TicketMetaData.TicketTable.KEY_Etapa + " text, "
                    + TicketMetaData.TicketTable.KEY_EstatusOp + " text, "
                    + TicketMetaData.TicketTable.KEY_EntreCalle1 + " text, "
                    + TicketMetaData.TicketTable.KEY_EntreCalle2 + " text, "
                    + TicketMetaData.TicketTable.KEY_DirGral + " text, "
                    + TicketMetaData.TicketTable.KEY_DirArea + " text, "
                    + TicketMetaData.TicketTable.KEY_Dependencia + " text, "
                    + TicketMetaData.TicketTable.KEY_Delegacion + " text default '',  "
                    + TicketMetaData.TicketTable.KEY_CP + " text default '', "
                    + TicketMetaData.TicketTable.KEY_ComentariosOp + " text, "
                    + TicketMetaData.TicketTable.KEY_ComentarioSI + " text, "
                    + TicketMetaData.TicketTable.KEY_ComentariosSF + " text,"
                    + TicketMetaData.TicketTable.KEY_Colonia + " text default '' , "
                    + TicketMetaData.TicketTable.KEY_Cantidad + " text, "
                    + TicketMetaData.TicketTable.KEY_Calle + " text default '',"
                    + TicketMetaData.TicketTable.KEY_Area + " text,"
                    + TicketMetaData.TicketTable.KEY_atendido + " text default 'No',"
                    + TicketMetaData.TicketTable.KEY_Vialidad + " text default '',"
                    + TicketMetaData.TicketTable.KEY_uploadImageResponse + " text default '',"
                    + TicketMetaData.TicketTable.KEY_Sincronizado + " text default 'Pendiente',"
                    + TicketMetaData.TicketTable.KEY_EstadoTicket + " integer default 0"

                    + ");";

    //Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DATABASE_CREATE);

    }

    /*
     * Esta clase tiene el objetivo de administrar el versionado de la base de
     * datos, asi como proporcionar la conexi�n al manejador SQLite cada vez que
     * se requiera
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TicketMetaData.DEBUG_TAG_OPENHELPER,
                "Upgrading database. Existing contents will be lost. ["
                        + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS "
                + TicketMetaData.TABLE_TICKETS);
        onCreate(db);

    }


}
