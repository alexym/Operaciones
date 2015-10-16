package com.agu.operaciones.providers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.agu.operaciones.providers.TicketMetaData.TicketTable;

import java.util.HashMap;
/**
 * Created by Cloudco on 14/10/15.
 */
public class TicketContentProvider extends ContentProvider {

    private DBHelper mDB;
    //Etiqueta de la clase para LOG
    public static final String  TAG = "TicketContentProvider";


    private static HashMap<String, String> ticketsProjectionMap;

    static {
        ticketsProjectionMap = new HashMap<String, String>();
        ticketsProjectionMap.put(TicketMetaData.TicketTable.KEY_ROWID,TicketTable.KEY_ROWID);
        ticketsProjectionMap.put(TicketTable.KEY_Tramo,TicketTable.KEY_Tramo);
        ticketsProjectionMap.put(TicketTable.KEY_UDeMedida,TicketTable.KEY_UDeMedida);
        ticketsProjectionMap.put(TicketTable.KEY_TipoAvenida,TicketTable.KEY_TipoAvenida);
        ticketsProjectionMap.put(TicketTable.KEY_Servicio,TicketTable.KEY_Servicio);
        ticketsProjectionMap.put(TicketTable.KEY_Sentido,TicketTable.KEY_Sentido);
        ticketsProjectionMap.put(TicketTable.KEY_PuntoReferencia,TicketTable.KEY_PuntoReferencia);
        ticketsProjectionMap.put(TicketTable.KEY_PrioridadSI,TicketTable.KEY_PrioridadSI);
        ticketsProjectionMap.put(TicketTable.KEY_Prioridad,TicketTable.KEY_Prioridad);
        ticketsProjectionMap.put(TicketTable.KEY_NumTicket,TicketTable.KEY_NumTicket);
        ticketsProjectionMap.put(TicketTable.KEY_Motivo,TicketTable.KEY_Motivo);
        ticketsProjectionMap.put(TicketTable.KEY_Materiales,TicketTable.KEY_Materiales);
        ticketsProjectionMap.put(TicketTable.KEY_LugarFisico,TicketTable.KEY_LugarFisico);
        ticketsProjectionMap.put(TicketTable.KEY_Longitud,TicketTable.KEY_Longitud);
        ticketsProjectionMap.put(TicketTable.KEY_Localizado,TicketTable.KEY_Localizado);
        ticketsProjectionMap.put(TicketTable.KEY_lImgSI,TicketTable.KEY_lImgSI);
        //ticketsProjectionMap.put(TicketTable.KEY_LongitudInicial,TicketTable.KEY_LongitudInicial);
        //ticketsProjectionMap.put(TicketTable.KEY_LatitudInicial,TicketTable.KEY_LatitudInicial);
        //ticketsProjectionMap.put(TicketTable.KEY_ImgSI,TicketTable.KEY_ImgSI);
        //ticketsProjectionMap.put(TicketTable.KEY_FechaHora,TicketTable.KEY_FechaHora);
        ticketsProjectionMap.put(TicketTable.KEY_Latitud,TicketTable.KEY_Latitud);
        ticketsProjectionMap.put(TicketTable.KEY_ImgOp3,TicketTable.KEY_ImgOp3);
        ticketsProjectionMap.put(TicketTable.KEY_ImgOp2,TicketTable.KEY_ImgOp2);
        ticketsProjectionMap.put(TicketTable.KEY_ImgOp1,TicketTable.KEY_ImgOp1);
        ticketsProjectionMap.put(TicketTable.KEY_ImgIng3,TicketTable.KEY_ImgIng3);
        ticketsProjectionMap.put(TicketTable.KEY_ImgIng2,TicketTable.KEY_ImgIng2);
        ticketsProjectionMap.put(TicketTable.KEY_ImgIng1,TicketTable.KEY_ImgIng1);
        ticketsProjectionMap.put(TicketTable.KEY_ImgSF1,TicketTable.KEY_ImgSF1);
        ticketsProjectionMap.put(TicketTable.KEY_ImgSF2,TicketTable.KEY_ImgSF2);
        ticketsProjectionMap.put(TicketTable.KEY_ImgSF3,TicketTable.KEY_ImgSF3);
        ticketsProjectionMap.put(TicketTable.KEY_IdTicket,TicketTable.KEY_IdTicket);
        ticketsProjectionMap.put(TicketTable.KEY_Grupo,TicketTable.KEY_Grupo);
        ticketsProjectionMap.put(TicketTable.KEY_GrupoServicios,TicketTable.KEY_GrupoServicios);
        ticketsProjectionMap.put(TicketTable.KEY_Etapa,TicketTable.KEY_Etapa);
        ticketsProjectionMap.put(TicketTable.KEY_EstatusOp,TicketTable.KEY_EstatusOp);
        ticketsProjectionMap.put(TicketTable.KEY_EntreCalle2,TicketTable.KEY_EntreCalle2);
        ticketsProjectionMap.put(TicketTable.KEY_EntreCalle1,TicketTable.KEY_EntreCalle1);
        ticketsProjectionMap.put(TicketTable.KEY_DirGral,TicketTable.KEY_DirGral);
        ticketsProjectionMap.put(TicketTable.KEY_DirArea,TicketTable.KEY_DirArea);
        ticketsProjectionMap.put(TicketTable.KEY_Descripcion,TicketTable.KEY_Descripcion);
        ticketsProjectionMap.put(TicketTable.KEY_Dependencia,TicketTable.KEY_Dependencia);
        ticketsProjectionMap.put(TicketTable.KEY_Delegacion,TicketTable.KEY_Delegacion);
        ticketsProjectionMap.put(TicketTable.KEY_CP,TicketTable.KEY_CP);
        ticketsProjectionMap.put(TicketTable.KEY_ComentariosOp,TicketTable.KEY_ComentariosOp);
        ticketsProjectionMap.put(TicketTable.KEY_ComentarioSI,TicketTable.KEY_ComentarioSI);
        //ticketsProjectionMap.put(TicketTable.KEY_ComentariosSI,TicketTable.KEY_ComentariosSI);
        ticketsProjectionMap.put(TicketTable.KEY_ComentariosSF,TicketTable.KEY_ComentariosSF);
        ticketsProjectionMap.put(TicketTable.KEY_Colonia,TicketTable.KEY_Colonia);
        ticketsProjectionMap.put(TicketTable.KEY_Cantidad,TicketTable.KEY_Cantidad);
        ticketsProjectionMap.put(TicketTable.KEY_Calle,TicketTable.KEY_Calle);
        ticketsProjectionMap.put(TicketTable.KEY_Area,TicketTable.KEY_Area);
        ticketsProjectionMap.put(TicketTable.KEY_Sincronizado,TicketTable.KEY_Sincronizado);
        ticketsProjectionMap.put(TicketTable.KEY_Vialidad,TicketTable.KEY_Vialidad);
        ticketsProjectionMap.put(TicketTable.KEY_uploadImageResponse,TicketTable.KEY_uploadImageResponse);
        ticketsProjectionMap.put(TicketTable.KEY_atendido,TicketTable.KEY_atendido);
        ticketsProjectionMap.put(TicketTable.KEY_Procede,TicketTable.KEY_Procede);
        ticketsProjectionMap.put(TicketTable.KEY_EstadoTicket,TicketTable.KEY_EstadoTicket);
        //ticketsProjectionMap.put(TicketTable.KEY_EtapaSupervision,TicketTable.KEY_EtapaSupervision);


    }
    private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
    static {
        SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
        SEARCH_SUGGEST_PROJECTION_MAP.put(TicketTable._ID, TicketTable._ID );
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, TicketTable.KEY_NumTicket + " AS "   + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, TicketTable.KEY_Etapa + " AS "    + SearchManager.SUGGEST_COLUMN_TEXT_2);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, TicketTable._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }


    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher
                .addURI(TicketMetaData.AUTHORITY,
                        TicketMetaData.TicketTable.TICKETS_BASE_PATH,
                        TicketMetaData.TicketTable.TICKETS);
        sURIMatcher
                .addURI(TicketMetaData.AUTHORITY,
                        TicketMetaData.TicketTable.TICKETS_BASE_PATH
                                + "/#",
                        TicketMetaData.TicketTable.TICKET_ID);
        // to get suggestions...
        sURIMatcher.addURI(TicketMetaData.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, TicketTable.SEARCH_SUGGEST);
        sURIMatcher.addURI(TicketMetaData.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", TicketTable.SEARCH_SUGGEST);

    }


    @Override
    public boolean onCreate() {
        Log.d(TicketMetaData.DEBUG_TAG_OPENHELPER, "main onCreate Called");
        mDB = new DBHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case TicketMetaData.TicketTable.TICKETS:
                rowsAffected = sqlDB.delete(TicketMetaData.TABLE_TICKETS,selection, selectionArgs);
                break;
            case TicketMetaData.TicketTable.TICKET_ID:
                String id = uri.getLastPathSegment();
//	        if (TextUtils.isEmpty(selection)) {
//	            rowsAffected = sqlDB.delete(ContentProviderMetaData.TABLE_TUTORIALS,
//	            		ContentProviderMetaData.ID + "=" + id, null);
//	        } else {
//	            rowsAffected = sqlDB.delete(ContentProviderMetaData.TABLE_TUTORIALS,
//	                    selection + " and " + ContentProviderMetaData.ID + "=" + id,
//	                    selectionArgs);
//	        }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case TicketTable.TICKETS :
                return TicketTable.CONTENT_TYPE;
            case TicketTable.TICKET_ID:
                return TicketTable.CONTENT_ITEM_TYPE;
//            case TicketTable.REFRESH_SHORTCUT:
//                return SearchManager.SHORTCUT_MIME_TYPE;
            case TicketTable.SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        //Verificando la petici�n
        if (sURIMatcher.match(uri) != TicketMetaData.TicketTable.TICKETS) {
            throw new IllegalArgumentException("Uknowkn Uri "+uri);
        }

        ContentValues contentValues;
        //validamos que no vengan vacios
        if(initialValues != null){
            contentValues = new ContentValues(initialValues);
        }else
        {
            contentValues = new ContentValues();
        }

        SQLiteDatabase db = mDB.getWritableDatabase();
        long rowID = db.insert(TicketTable.TABLE_NAME, TicketTable.TABLE_NAME, contentValues);


        if(rowID > 0)
        {
            Uri insertedTicketUri = ContentUris.withAppendedId(TicketTable.CONTENT_URI, rowID);
            //Notificamos la inserción al content Resolver que nos invoco
            getContext().getContentResolver().notifyChange(insertedTicketUri, null);

            return insertedTicketUri;
        }

        throw new SQLiteException("Error al insertar ticket : "+uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TicketMetaData.TABLE_TICKETS);
        queryBuilder.setProjectionMap(ticketsProjectionMap);

        queryBuilder.setTables(TicketMetaData.TABLE_TICKETS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TicketTable.TICKET_ID:
                Log.i(TAG,"tiene filtro");
                queryBuilder.appendWhere(TicketMetaData.ID + "=" + uri.getLastPathSegment());
                queryBuilder.setProjectionMap(ticketsProjectionMap);
                break;
            case TicketTable.TICKETS:
                Log.i(TAG,"no tiene filtro");
                queryBuilder.setProjectionMap(ticketsProjectionMap);
                break;
            case TicketTable.SEARCH_SUGGEST:
                Log.i(TAG,"searchSuggest");
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                selectionArgs = new String[] { "%" + selectionArgs[0] + "%" };
                queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
                break;
            default:
                throw new IllegalArgumentException("URI DESCONOCIDA" + uri);

        }
        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case TicketTable.TICKETS:
                rowsUpdated = sqlDB.update(TicketTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case TicketTable.TICKET_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(TicketTable.TABLE_NAME,
                            values,
                            TicketTable.KEY_ROWID + "=" + id,
                            null);

                } else {
                    rowsUpdated = sqlDB.update(TicketTable.TABLE_NAME,
                            values,
                            TicketTable.KEY_ROWID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
