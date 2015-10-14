package com.agu.operaciones.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cloudco on 14/10/15.
 */
public class TicketMetaData {
    private TicketMetaData() {}

    public static final String AUTHORITY = "com.agu.operaciones";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "SupervisorDB";
    public static final String TABLE_TICKETS = "tickets";
    public static final String ID = "_id";
    public static final String DEBUG_TAG_OPENHELPER = "SQLiteOpenHelperData";

    /*
     * Constantes sincronizado
     */
    public static final String CONST_SINCRONIZADO_AUN_NO = "Pendiente";
    public static final String CONST_SINCRONIZADO_SI = "Sincronizado";
    public static final String CONST_SINCRONIZADO_NO = "ErrorSincronizacion";
	/*
	 * Constantes de webservice
	 */

    public static final String CONST_ETAPA_SUPERVISION = "Supervisión";
    public static final String CONST_ETAPA_CONCLUSION = "Concluido";
    public static final String CONST_ETAPA_INGRESO = "Ingreso";

    /*
     * Constante nombre SharedPreferences
     */
    public static final String SP_TICKET = "ticketSP";
    /*
     * Constante nombre latitud y longitud
     */
    public static final String LATITUD = "lat";
    public static final String LONGITUD = "lon";
    /*
     *Estado Ticket si esta abierto o cerrado
     */
    public static final int	CONST_TICKET_CERRADO = 0;
    public static final int	CONST_TICKET_ABIERTO = 1;
    /*
    *Estado inicio de sesión
    */
    public static final String DESCARGA_TICKETS_INICIO = "DescargaTicketsInicio";

    /*
     * Clase en donde se define la entidad de la tabla
     */
    public static final class TicketTable implements BaseColumns {

        private TicketTable() {}

        public static final String TABLE_NAME = TABLE_TICKETS;
        public static final int TICKETS = 100;
        public static final int TICKET_ID = 110;
        public static final int SEARCH_SUGGEST = 120;


        /**
         * DEfinición del Content URI y MIMEs
         */

        public static final String TICKETS_BASE_PATH = "tickets";
        public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/" + TICKETS_BASE_PATH);
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/com.AGU.supervisorKitKat.ticket";// "vnd.android.cursor.item//com.AGU.supervisorKitKat.ticket";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/com.AGU.supervisorKitKat.ticket";// "vnd.android.cursor.dir//com.AGU.supervisorKitKat.ticket";
        /**
         * Orden por defecto de la tabla
         */
        public static final String DEFAULT_SORT_ORDER = "_ID DESC";

        /**
         * Orden personalizado
         */
        public static final String SORT_BY_NAME_DESC = "name DESC";
        public static final String SORT_BY_NAME_ASC = "name ASC";

		/*
		 * Propiedades de la entidad
		 */

        public static final String KEY_ROWID = "_id";
        public static final String KEY_Tramo = "Tramo";
        public static final String KEY_TipoAvenida = "TipoAvenida";
        public static final String KEY_Servicio = "Servicio";
        public static final String KEY_Sentido = "Sentido";
        public static final String KEY_PuntoReferencia = "PuntoReferencia";
        public static final String KEY_PrioridadSI = "PrioridadSI";
        public static final String KEY_Prioridad = "Prioridad";
        public static final String KEY_NumTicket = "NumTicket";
        public static final String KEY_Motivo = "Motivo";
        public static final String KEY_Materiales = "Materiales";
        public static final String KEY_LugarFisico = "LugarFisico";
        public static final String KEY_Longitud = "Longitud";
        public static final String KEY_Localizado = "Localizado";
        public static final String KEY_lImgSI = "lImgSI";
        public static final String KEY_LongitudInicial = "LongitudInicial";
        public static final String KEY_LatitudInicial = "LatitudInicial";
        public static final String KEY_ImgSI = "ImgSI";
        public static final String KEY_FechaHora = "FechaHora";
        public static final String KEY_Latitud = "Latitud";
        public static final String KEY_ImgOp3 = "ImgOp3";
        public static final String KEY_ImgOp2 = "ImgOp2";
        public static final String KEY_ImgOp1 = "ImgOp1";
        public static final String KEY_ImgIng3 = "ImgIng3";
        public static final String KEY_ImgIng2 = "ImgIng2";
        public static final String KEY_ImgIng1 = "ImgIng1";
        public static final String KEY_ImgSF1 = "ImgSF1";
        public static final String KEY_ImgSF2 = "ImgSF2";
        public static final String KEY_ImgSF3 = "ImgSF3";
        public static final String KEY_IdTicket = "IdTicket";
        public static final String KEY_Grupo = "Grupo";
        public static final String KEY_GrupoServicios = "GpoServicios";
        public static final String KEY_Etapa = "Etapa";
        public static final String KEY_EstatusOp = "EstatusOp";
        public static final String KEY_EntreCalle2 = "EntreCalle2";
        public static final String KEY_EntreCalle1 = "EntreCalle1";
        public static final String KEY_DirGral = "DirGral";
        public static final String KEY_DirArea = "DirArea";
        public static final String KEY_Descripcion = "Descripcion";
        public static final String KEY_Dependencia = "Dependencia";
        public static final String KEY_Delegacion = "Delegacion";
        public static final String KEY_CP = "CP";
        public static final String KEY_ComentariosOp = "ComentariosOp";
        public static final String KEY_ComentarioSI = "ComentarioSI";
        public static final String KEY_ComentariosSI = "ComentariosSI";
        public static final String KEY_ComentariosSF = "ComentariosSF";
        public static final String KEY_Colonia = "Colonia";
        public static final String KEY_Cantidad = "Cantidad";
        public static final String KEY_Calle = "Calle";
        public static final String KEY_Area = "Area";
        public static final String KEY_Sincronizado = "EdoTel";
        public static final String KEY_Vialidad = "Vialidad";
        public static final String KEY_uploadImageResponse = "uploadImageResponse";
        public static final String KEY_atendido = "AtendidaSF";
        public static final String KEY_Procede = "Procede";
        public static final String KEY_EstadoTicket = "EstadoTicket";
        public static final String KEY_EtapaSupervision = "";
    }
}
