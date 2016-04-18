package mundo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.cejjr.reportapp.VerReportesActivity;
import com.cejjr.reportapp.VerReportesOnlineActivity;
import com.squareup.okhttp.FormEncodingBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


/**
 * Created by Usuario Autorizado on 11/04/2016.
 */
public class RestClient {

    private static String BASE_URL = "http://ujkka6078b18.juanjorogo.koding.io:3000";
    private JSONArray reportesResume;

    OkHttpClient client = new OkHttpClient();
    //UTILS

    public void setReportes(JSONArray that){
        this.reportesResume = that;
    }

    public JSONArray getReportes(){
        return this.reportesResume;
    }

    //
    //METODOS API
    //
    /**
     * Metodo para recibir los reportes.
     * @throws IOException
     */
    public void getAllReportes() throws IOException{
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(BASE_URL+"/reportes")
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    /**
     * Metodo para recibir los reportes.
     * @throws IOException
     */
    public void getIdentifiers() throws IOException{
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(BASE_URL+"/reportesList")
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray reportes = new JSONArray(response.body().string());
                    ArrayList<String> reportesText = new ArrayList<String>();

                        for (int i = 0; i < reportes.length(); i++) {
                            JSONObject js = reportes.getJSONObject(i);
                            String id = js.getString("identificador");
                            reportesText.add(id);
                        }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Metodo usado para crear un reporte.
     * @param reporte
     * @param file
     * @throws IOException
     */
    public void upload( Reporte reporte, File file) throws IOException{
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", reporte.id+"_"+file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL+"/upload")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    /**
     * Metodo usado para crear un reporte.
     * @param reporte
     * @throws IOException
     */
    public void post(Reporte reporte, int numero) throws Exception{
        RequestBody formBody = new FormBody.Builder()
                .add("message", reporte.getAsunto())
                .add("identificador", reporte.id)
                .add("numImages", String.valueOf(numero) )
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL+"/reportes")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    public void submitComment(String origen, String destino,String comment) throws Exception{
        RequestBody formBody = new FormBody.Builder()
                .add("idOrigen", origen)
                .add("idDestino", destino)
                .add("comentario", comment)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL+"/comentarios")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    /**
     * Metodo utilizado para descargas las imagenes de cada reporte.
     * @param reporteId
     * @throws IOException
     */
    public void download(String reporteId) throws IOException{
        Request request = new Request.Builder()
                .url("https://pbs.twimg.com/profile_images/61655057/2425718692_1783fe0913_b.jpg")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ERROR","No se descarga imagen");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                long contentLength = body.contentLength();
                BufferedSource source = body.source();

                File file = new File(Environment.getExternalStorageDirectory()+"/ReportApp/"+ "imagen.jpg");
                BufferedSink sink = Okio.buffer(Okio.sink(file));

                long bytesRead = 0;
                while (source.read(sink.buffer(), 2048) != -1) {
                    bytesRead += 2048;
                    int progress = (int) ((bytesRead * 100) / contentLength);
                }
                sink.writeAll(source);
                sink.close();

            }
        });
    }

    /**
     * NO lo estoy usando pero mejor lo dejo
     * @param imageData
     * @return
     */
    private String storeImage(Bitmap imageData) {
        // get path to external storage (SD card)
        String filePath = null;
        if (imageData != null) {


            try {
                //boolean s = sdIconStorageDir.mkdirs();
                filePath = Environment.getExternalStorageState()
                        + System.currentTimeMillis() + ".jpg";
                Log.d("Donde", filePath);
                FileOutputStream fileOutputStream = new FileOutputStream(
                        filePath);

                BufferedOutputStream bos = new BufferedOutputStream(
                        fileOutputStream);

                // choose another format if PNG doesn't suit you
                imageData.compress(Bitmap.CompressFormat.JPEG, 0, bos);

                bos.flush();
                bos.close();
                return filePath;
            } catch (IOException e) {
                return "fail";
            }
        } else {
            return "fail";
        }

    }



}
