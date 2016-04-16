package mundo;

import com.squareup.okhttp.FormEncodingBuilder;

import java.io.File;
import java.io.IOException;

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



/**
 * Created by Usuario Autorizado on 11/04/2016.
 */
public class RestClient {

    private static String BASE_URL = "http://157.253.209.197:3000";

    OkHttpClient client = new OkHttpClient();

    /**
     * Metodo para recibir los reportes.
     * @param url
     * @throws IOException
     */
    public void get(String url) throws IOException{
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
    public void post(Reporte reporte) throws IOException{
        RequestBody formBody = new FormBody.Builder()
                .add("message", reporte.getAsunto())
                .add("identificador", reporte.id)
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

}
