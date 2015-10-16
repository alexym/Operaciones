package com.agu.operaciones.webservices;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.agu.operaciones.SplashActivity;
import com.agu.operaciones.utilities.NoInternetException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Cloudco on 14/10/15.
 */
public class DataWebservice {

    public final static String TAG = "DataWebservice";
    // cONSTANTES METODOWS DE CONEXION
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    // HashMap que tendra las constantes de los servicios a utilizar
    public static final HashMap<String, String> urlMap = new HashMap<String, String>();
    public static final String REPORTE = "REPORTE";
    public static final String IMAGE_SUP = "IMAGE_SUPERVISION";
    public static final String IMAGE_CONCL = "IMAGE_CONCLUSION";
    public static final String IMAGE_OPERACION = "IMAGE_OPERACION";
    public static final String TOKEN = "TOKEN";
    public static final String SUPERVISOR = "SUPERVISOR";
    public static final String SINCRONIZAR = "SINCRONIZAR";
    public static final String LOGOUT = "LOGOUT";
    public static final String VALIDARSINC = "VALIDARSINC";
    public static final String DIRECCIONES = "DIRECCIONES";
    public static final String SMACTTICKET = "SMACTTICKET";
    public static final String SMINSTICKET = "SMINSTICKET";
    public static final String URLCUSTOMSUPINI = "URLCUSTOMSUPINIC";
    public static final String URLCUSTOMSUPFIN = "URLCUSTOMSUPIFIN";
    public static final String URLCUSTOMOPERACION = "URLCUSTOMOPERACION";

    public static String params;

    static InputStream is = null;

    static {

		/*
		 * Ambiente de Pruebas
		 */

        if (SplashActivity.MODO_DEBUG) {

            // SANDBOX
            // params =
            // "grant_type=password&client_id=3MVG9dPGzpc3kWyfOCD.QECg8sdItLn2rN5njiSZrJtI3rMP2es6EwraFoW2UgcFdYUYqqivJtJg6RTIps49A&client_secret=1434304286937802337&username=darzola@grupoday.com.dev&password=davidmaar2014TJWvF0OFRVxqzX2v7zEPU5rdX";
            // CAPACITA
            // params =
            // "grant_type=password&client_id=3MVG9dPGzpc3kWyfOCD.QECg8sdItLn2rN5njiSZrJtI3rMP2es6EwraFoW2UgcFdYUYqqivJtJg6RTIps49A&client_secret=1434304286937802337&username=darzola@grupoday.com.capacita&password=davidmaar20141zTmz8SUNe0Yn7XgEXfSwsntU";
            // CONFIG
            // params =
            // "grant_type=password&client_id=3MVG9dPGzpc3kWyfOCD.QECg8sdItLn2rN5njiSZrJtI3rMP2es6EwraFoW2UgcFdYUYqqivJtJg6RTIps49A&client_secret=1434304286937802337&username=darzola@grupoday.com.config&password=davidmaar201453YeEPU2yPEMsigJGmgcVQNa";
            // BACKCUPFULL
            //params = "grant_type=password&client_id=3MVG9dPGzpc3kWyfOCD.QECg8sdItLn2rN5njiSZrJtI3rMP2es6EwraFoW2UgcFdYUYqqivJtJg6RTIps49A&client_secret=1434304286937802337&username=adminconfiguracion@agucdmx.gob.mx.backupfull&password=grupoday0141ayBeGgqgnj8n6gXACerxU1";
            //params = "grant_type=password&client_id=3MVG99OxTyEMCQ3j1W8faf5mAqHImevYMg5eflaVYq27DG8riryIHpggEZZxLMUAE01vz_icavWX8vSpVCaus&client_secret=1110909413960982424&username=admin072movil@obrasdf.gob.mx&password=072movil2013aCVunihgEOWjjTV4Sp35MGjB";
            params = "grant_type=password&client_id=3MVG99OxTyEMCQ3j1W8faf5mAqHImevYMg5eflaVYq27DG8riryIHpggEZZxLMUAE01vz_icavWX8vSpVCaus&client_secret=1110909413960982424&username=adminconfiguracion@agucdmx.gob.mx.backupfull&password=grupoday01KKneBqdU9LKZeI1E9ObJCGDy";

            Log.i(TAG, params);
			/*
			 * urlMap.put(TOKEN,
			 * "https://cs23.salesforce.com/services/oauth2/token");
			 * urlMap.put(SUPERVISOR,
			 * "https://cs23.salesforce.com/services/apexrest/SMLogIn");
			 * urlMap.put(VALIDARSINC,
			 * "https://cs23.salesforce.com/services/apexrest/SMValidaSinc");
			 * urlMap.put(DIRECCIONES,
			 * "https://cs23.salesforce.com/services/apexrest/SMDirecciones");
			 * urlMap.put(SINCRONIZAR,
			 * "https://cs23.salesforce.com/services/apexrest/SMSincroniza");
			 * urlMap.put(LOGOUT,
			 * "https://cs23.salesforce.com/services/apexrest/SMLogOut");
			 * urlMap.put(SMACTTICKET,
			 * "https://cs23.salesforce.com/services/apexrest/SMActTicket");
			 * urlMap.put(SMINSTICKET,
			 * "https://cs23.salesforce.com/services/apexrest/SMInsTicket");
			 */

			/*urlMap.put(TOKEN,
					"https://cs24.salesforce.com/services/oauth2/token");
			urlMap.put(SUPERVISOR,
					" https://cs24.salesforce.com/services/apexrest/SMLogIn");
			urlMap.put(VALIDARSINC,
					"https://cs24.salesforce.com/services/apexrest/SMValidaSinc");
			urlMap.put(DIRECCIONES,
					"https://cs24.salesforce.com/services/apexrest/SMDirecciones");
			urlMap.put(SINCRONIZAR,
					"https://cs24.salesforce.com/services/apexrest/SMSincroniza");
			urlMap.put(LOGOUT,
					"https://cs24.salesforce.com/services/apexrest/SMLogOut");
			urlMap.put(SMACTTICKET,"https://cs24.salesforce.com/services/apexrest/SMActTicket");
			urlMap.put(SMINSTICKET,
					"https://cs24.salesforce.com/services/apexrest/SMInsTicket");*/

            urlMap.put(TOKEN,
                    "https://cs19.salesforce.com/services/oauth2/token");

            urlMap.put(SUPERVISOR,
                    "https://cs19.salesforce.com/services/apexrest/OMLogIn");
            urlMap.put(VALIDARSINC,
                    "https://cs19.salesforce.com/services/apexrest/OMValidaSinc");
            urlMap.put(DIRECCIONES,
                    "https://cs19.salesforce.com/services/apexrest/OMDirecciones");

            urlMap.put(SINCRONIZAR,
                    "https://cs19.salesforce.com/services/apexrest/OMSincroniza");
            urlMap.put(LOGOUT,
                    "https://cs19.salesforce.com/services/apexrest/OMLogOut");
            urlMap.put(SMACTTICKET,"https://cs19.salesforce.com/services/apexrest/OMActTicket");
            urlMap.put(SMINSTICKET,
                    "https://cs19.salesforce.com/services/apexrest/OMInsTicket");


            // IPANTIGUA 189.254.14.169
//            urlMap.put(IMAGE_SUP,
//                    "http://simgweb.072cdmx.gob.mx/supmovil/uploadSupervision.php");

//
//            urlMap.put(URLCUSTOMSUPINI,
//                    "http://simgweb.072cdmx.gob.mx/supmovil/Supervisi%C3%B3n%20inicial/");
//            urlMap.put(IMAGE_OPERACION,
//                    "http://simgweb.072cdmx.gob.mx/imagen/AGENCIA%20DE%20GESTI%C3%93N%20URBANA/Operaci%C3%B3n/uploadOperacion.php");
            urlMap.put(IMAGE_OPERACION,
                    "http://simgweb.072cdmx.gob.mx/supmovil/uploadOperacion.php");
            urlMap.put(URLCUSTOMOPERACION,
                    "http://simgweb.072cdmx.gob.mx/supmovil//Operaci%C3%B3n/");
//                    "http://simgweb.072cdmx.gob.mx/imagen/AGENCIA%20DE%20GESTI%C3%93N%20URBANA/Operaci%C3%B3n/SOBSE/");

        } else {

			/*
			 * Ambiente de Producción
			 */
            params = "grant_type=password&client_id=3MVG99OxTyEMCQ3j1W8faf5mAqHImevYMg5eflaVYq27DG8riryIHpggEZZxLMUAE01vz_icavWX8vSpVCaus&client_secret=1110909413960982424&username=admin072movil@obrasdf.gob.mx&password=072movil2013aCVunihgEOWjjTV4Sp35MGjB";
            urlMap.put(TOKEN,
                    "https://na19.salesforce.com/services/oauth2/token");
            urlMap.put(SUPERVISOR,
                    " https://na19.salesforce.com/services/apexrest/SMLogIn");
            urlMap.put(VALIDARSINC,
                    "https://na19.salesforce.com/services/apexrest/SMValidaSinc");
            urlMap.put(DIRECCIONES,
                    "https://na19.salesforce.com/services/apexrest/SMDirecciones");
            urlMap.put(SINCRONIZAR,
                    "https://na19.salesforce.com/services/apexrest/SMSincroniza");
            urlMap.put(LOGOUT,
                    "https://na19.salesforce.com/services/apexrest/SMLogOut");
            urlMap.put(SMACTTICKET,
                    "https://na19.salesforce.com/services/apexrest/SMActTicket");
            urlMap.put(SMINSTICKET,
                    "https://na19.salesforce.com/services/apexrest/SMInsTicket");
            urlMap.put(IMAGE_SUP,
                    "http://simgweb.072cdmx.gob.mx/imagen/uploadSupervision.php");
            urlMap.put(IMAGE_CONCL,
                    "http://simgweb.072cdmx.gob.mx/imagen/uploadConclusion.php");
            urlMap.put(
                    URLCUSTOMSUPINI,
                    "http://simgweb.072cdmx.gob.mx/imagen/AGENCIA%20DE%20GESTI%C3%93N%20URBANA/Supervisi%C3%B3n%20inicial/");
            urlMap.put(
                    URLCUSTOMSUPFIN,
                    "http://simgweb.072cdmx.gob.mx/imagen/AGENCIA%20DE%20GESTI%C3%93N%20URBANA/Supervisi%C3%B3n%20final/");

        }

    }

    private static String getToken() throws IOException {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(urlMap.get(TOKEN));
            System.out.println("Token URL " + url);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // connection.setRequestProperty("Content-Length",
            // Integer.toString(params.length()));
            connection.setUseCaches(false);

            writeRequest(connection, params);
            String resp = readResponse(connection);
            resp = replaceAll(resp, "\"{", "{");
            resp = replaceAll(resp, "}\"", "}");
            resp = replaceAll(resp, "\\\"", "\'");
            JSONObject obj = new JSONObject(resp);
            System.out.println("TOKEN " + obj);
            return obj.getString("access_token");
        } catch (Exception e) {
            System.out.println("Hubo una excepcion " + e.getClass());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        return "No token";

    }

    // Metodo que escribe la peticion a una conexion
    private static boolean writeRequest(HttpsURLConnection connection,
                                        String textBody) {

        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));

            wr.write(textBody);
            wr.flush();
            wr.close();
            return true;
        } catch (IOException e) {

            return false;
        }
    }

    private static boolean writeRequest(HttpURLConnection connection,
                                        String textBody) {
        Log.i("writeRequest", textBody);
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
                    connection.getOutputStream()));

            wr.write(textBody);
            wr.flush();
            wr.close();
            return true;
        } catch (IOException e) {
            Log.i("error en WriteRequest", e.getMessage());
            return false;
        }
    }

    // Lee una respuesta por una conexion dada y regresa una respuesta como un
    // string
    private static String readResponse(HttpsURLConnection connection) {
        try {
            StringBuilder str = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator")
                );

            }
            return str.toString();
        } catch (IOException e) {
            Log.e("->ReadResponse https", e.getMessage());
            return new String();
        }
    }
/*
	private static String readResponse(HttpURLConnection connection) {
		try {
			StringBuilder str = new StringBuilder();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {str.append(line + System.getProperty("line.separator"));
            }
			return str.toString();
		} catch (IOException e) {
			Log.e(TAG + "->ReadResponse http", e.getMessage());
			return new String();
		}
	}*/

    private static String readResponseUploadImage(HttpURLConnection connection) {
        try {
            StringBuilder str = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append(line);

            }
            return str.toString();
        } catch (IOException e) {
            Log.e("readResponse imagen", e.getMessage());
            return new String();
        }
    }

    // Metodo que realiza el remplazo de cadenas para el objeto JSON
    public static String replaceAll(String source, String pattern,
                                    String replacement) {
        if (source == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        int idx = -1;
        int patIdx = 0;

        while ((idx = source.indexOf(pattern, patIdx)) != -1) {
            sb.append(source.substring(patIdx, idx));
            sb.append(replacement);
            patIdx = idx + pattern.length();
        }
        sb.append(source.substring(patIdx));
        return sb.toString();
    }

	/*
	 * Metodo para llamar servicio enviando un JSON por metodo POST
	 */

    public static JSONObject callService(String serviceName,
                                         final JSONObject param, Activity context)
            throws NoInternetException {

        // validacion de internet

        ConnectivityManager connManger = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManger.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {

            HttpsURLConnection connection = null;
            String resp = "";

            try {

                // Se obtiene el token
                String accessToken = getToken();
                String serv = urlMap.get(serviceName);

                URL url = new URL(serv);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod(DataWebservice.METHOD_POST);
                connection.setRequestProperty("Authorization", "OAuth "
                        + accessToken);
                connection.setRequestProperty("Content-Type",
                        "application/json;charset=UTF-8");
                connection.setUseCaches(false);
                writeRequest(connection, param.toString());
                resp = readResponse(connection);

                // Parse the JSON response into a JSON mapped object to fetch
                // fields
                // from.
                resp = replaceAll(resp, "\"{", "{");
                resp = replaceAll(resp, "}\"", "}");
                resp = replaceAll(resp, "\\\"", "\'");
                JSONObject obj = null;

                obj = new JSONObject(resp);

                return obj;

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            throw new NoInternetException();
        }

        return new JSONObject();

    }

	/*
	 * Metodo para llamar servicio ya con el token asignado
	 */

    public static JSONObject callService(String serviceName,
                                         final List<NameValuePair> params, String method, Activity context)
            throws NoInternetException {
        HttpsURLConnection connection = null;
        String resp = "";

        ConnectivityManager connManger = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManger.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {

            try {

                // Se obtiene el token
                String accessToken = getToken();
                String serv = urlMap.get(serviceName);
                if (method.equals(DataWebservice.METHOD_GET)) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    Log.d("CallService",
                            "Hashmap valor con GET = " + paramString);
                    URL url = new URL(serv + "?" + paramString);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestProperty("Authorization", "OAuth "+ accessToken);
                    connection.setRequestProperty("Content-Type",
                            "application/json;charset=UTF-8");
                    connection.setUseCaches(false);
                    connection.connect();

                    System.out.println(TAG + "Responsecode"
                            + connection.getResponseCode());
                    resp = readResponse(connection);
                    connection.disconnect();

                } else if (method.equals(DataWebservice.METHOD_POST)) {
                    JSONObject paramsJSON = new JSONObject();
                    for (NameValuePair p : params) {
                        paramsJSON.put(p.getName(), p.getValue());
                    }
                    Log.d("CallService",
                            "JSON valor con Post : " + paramsJSON.toString());
                    URL url = new URL(serv);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Authorization", "OAuth "
                            + accessToken);
                    connection.setRequestProperty("Content-Type",
                            "application/json;charset=UTF-8");
                    connection.setUseCaches(false);
                    writeRequest(connection, paramsJSON.toString());
                    resp = readResponse(connection);

                } else {

                    Log.e("callService",
                            "ERROR, METODO NO SOPORTADO");
                }

                // Parse the JSON response into a JSON mapped object to fetch
                // fields
                // from.
                resp = replaceAll(resp, "\"{", "{");
                resp = replaceAll(resp, "}\"", "}");
                resp = replaceAll(resp, "\\\"", "\'");
                JSONObject obj = null;
                // System.out.println("Respuesta " + resp);

                obj = new JSONObject(resp);

                return obj;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("callService()", e.getMessage());
            }

        } else {

            throw new NoInternetException();
        }

        return new JSONObject();

    }

	/*
	 * M�todo para descargar los tickets o descargar uno individual
	 */
    /*
	public static JSONObject callSMSincroniza(final List<NameValuePair> params,Activity context) throws NoInternetException {
		HttpsURLConnection connection = null;
		String resp = "";

		JSONObject responseObj = null;
		ConnectivityManager connManger = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManger.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			try {

				// Se obtiene el token
				String accessToken = getToken();
				String serv = urlMap.get(SINCRONIZAR);
				String paramString = URLEncodedUtils.format(params, "utf-8");

				URL url = new URL(serv + "?" + paramString);
				Log.d("DataWebservice->CallSMSincroniza", url.toExternalForm());
				connection = (HttpsURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Authorization", "OAuth " + accessToken);
				connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
				connection.setUseCaches(false);
				connection.connect();
				resp = readResponse(connection);

				connection.disconnect();
				// from.
				resp = replaceAll(resp, "\"{", "{");
				resp = replaceAll(resp, "}\"", "}");
				resp = replaceAll(resp, "\\\"", "\'");

				responseObj = new JSONObject(resp);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("DataWebservice->callSMSincroniza()", e.getMessage());
			}

		} else {

			throw new NoInternetException();
		}

		return responseObj;

	}*/

	/*
	 * Metodo para carga de imagenes
	 */

    public static String uploadImage(File sourceFile, String nameImage,
                                     String urlKEY) throws SocketTimeoutException, SocketException {

        int serverResponseCode = 0;

        String resp = "";

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        // File sourceFile = new File();
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :");
            return "";
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(urlMap.get(urlKEY));
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", "archivos");
                conn.setConnectTimeout(5000);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"nombre\""
                        + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(nameImage + lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + "archivo" + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                resp = readResponseUploadImage(conn);

                if (serverResponseCode == 200) {
                    Log.i(TAG, resp + "- -"
                            + serverResponseMessage);

                } else {
                    Log.i("fallo envio de imagen", "fallo");
                    resp = "Fallo la conexion de la subida de imagen  el serverresponsecode"+serverResponseCode;
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                // dialog.dismiss();
                ex.printStackTrace();
                Log.i("File Upload Completed",
                        "MalformedURLException Exception : check script url.");

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // dialog.dismiss();
            return resp;

        } // End else block
    }
}

//<br /><b>Warning</b>:  Unknown: failed to open stream: No such file or directory in <b>Unknown</b> on line <b>0</b><br /><br /><b>Fatal error</b>:  Unknown: Failed opening required 'D:/xampp/htdocs/imagen/AGENCIA DE GESTIÓN URBANA/Operación/uploadOperacion.php' (include_path='.;D:\xampp\php\PEAR') in <b>Unknown</b> on line <b>0</b><br />- -OK
