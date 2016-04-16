package mundo;

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

    OkHttpClient client = new OkHttpClient();

    /**
     * Metodo para recibir los reportes.
     * @param url
     * @throws IOException
     */
    public void get(String url) throws IOException{
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
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
     * @param url
     * @param reporte
     * @param file
     * @throws IOException
     */
    public void upload(String url, Reporte reporte, File file) throws IOException{
        RequestBody formBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
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
     * @param url
     * @param reporte
     * @param file
     * @throws IOException
     */
    public void post(String url, Reporte reporte, File file) throws IOException{
        RequestBody formBody = new MultipartBody.Builder()
                .addFormDataPart("message", "thisisthemessage")
                .addFormDataPart("id", reporte.id)
                .addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
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
     * Test to send an image to certain report
     * @param file
     * @throws Exception
     */
    public void image(String url, File file) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("text/x-markdown"), file))
                .build();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Fails sending file");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}
