package com.agu.operaciones.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Cloudco on 14/10/15.
 */
public class FileCameraManager {

    private static String idTicket;
    public static int path_supervisor = 1;
    public static int path_reporte = 2;
    public static int path_general = 3;
    public static String path;
    public static String generalPath;
    private static Context contexto;
    private static File mediaStorageDir;
    final String TAG = "FileCameraManager";


    public FileCameraManager(Context context,String id_ticket,int getPath) {
        idTicket = id_ticket;
        contexto = context;
        generalPath = contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        if(getPath == path_supervisor){
            path = generalPath+"/tickets/"+idTicket;
        }else if(getPath == path_reporte){
            path = generalPath+"/ReporteFotos/"+idTicket;
        }else if(getPath == path_general){
            path = generalPath;
        }
        creaDirectorio();
    }

    /** Create a file Uri for saving an image or video */
    public Uri getOutputMediaFileUri(String nombreImagen) {
        return Uri.fromFile(getOutputMediaFile(nombreImagen));
    }
    private  void creaDirectorio(){
        mediaStorageDir = new File(path);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            try{
                mediaStorageDir.mkdirs();
                Log.i(TAG, "->creaDirectorio " + mediaStorageDir.getPath());
            }catch(Exception e){
                Log.e(TAG,"FILE DIRECTORY "+e.getMessage());
            }
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(String nombreImg) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(
        //	Environment
        //		.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        //"MyCameraApp");

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + nombreImg);
        Log.i(TAG,"en getOutPutMedia");
        return mediaFile;
    }
    public static void DeleteRecursive(File fileOrDirectory) {
        System.out.println(fileOrDirectory.getPath());
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            {
                child.delete();
                DeleteRecursive(child);
            }

        fileOrDirectory.delete();
    }

    public boolean renameDirectory(String nuevoNombre){
        File oldDirectory = new File(path);
        File newDirectory = new File(generalPath+"/ReporteFotos/"+nuevoNombre);

        return oldDirectory.renameTo(newDirectory);
    }
    public boolean renameTempDirectory(String nuevoNombre){
        File oldDirectory = new File(path);
        File newDirectory = new File(generalPath+"/ReporteFotos/" + nuevoNombre);

        return oldDirectory.renameTo(newDirectory);
    }


}
