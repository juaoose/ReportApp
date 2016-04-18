package com.cejjr.reportapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VerDetallesOnline extends AppCompatActivity {
    String BASE_URL = "http://ujkka6078b18.juanjorogo.koding.io:3000";
    private TextView textReporte;
    Handler mHandler;
    private String ident;
    private int numeroI;
    LinearLayout layout;

    private void setText(String parametro){
        textReporte = ((TextView)findViewById(R.id.textView7));
        textReporte.setText(parametro);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalles_online);

        //Get reporte name
        Intent i = getIntent();
        String nombre = i.getStringExtra("reporte");

        //Dorest shit
        layout = (LinearLayout) findViewById(R.id.layoutImagen);
        mHandler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(BASE_URL+"/reportes/"+nombre)
                .build();
        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string().replace("\\","").replace("\"{","{").replace("}\",","},").replace("}\"","}");
                    JSONArray reportesz = new JSONArray(json);
                    JSONObject reportes = reportesz.getJSONObject(0);
                    final String identifier = reportes.getString("identificador");
                    final String comment = reportes.getString("message");
                    final int numeroImagenes = Integer.valueOf(reportes.getString("numImages"));
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            setNumbers(identifier,numeroImagenes);
                          setText(identifier+ ": "+comment);
                            setUpImages();

                           // setUpImages(identifier,numeroImagenes);
                        }
                    });


                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        //layout = (LinearLayout) findViewById(R.id.layoutImagen);
        //setUpImages();
    }

    private void setNumbers(String a, int b){
        this.ident = a;
        this.numeroI = b;
    }

    private void addImage(Bitmap cual){
        ImageView image = new ImageView(VerDetallesOnline.this);
        image.setImageBitmap(cual);
        layout.addView(image);
    }

    private void setUpImages(){
        Log.d("HELLLLLLPP","Intento numero"+numeroI);
        Log.d("HELLLLLLPP","Intento numero"+ident);
        for (int i=0; i<numeroI; i++) {

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/downloado/" + textReporte.getText().toString().split(":")[0] + "/" +(i+1))
                    .build();
            Response response = null;
            client.newCall(request).enqueue(new Callback(){

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("NOPE","NOPE");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                       final Bitmap icon = BitmapFactory.decodeStream(response.body().byteStream());
                        mHandler.post(new Runnable(){
                            @Override
                            public void run(){
                                addImage(icon);
                            }
                        });


                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            Log.d("Hello","u dere");
        }
        ident = "";
        numeroI = 0;

    }

    @Override
    public void onBackPressed(){
        Intent it = new Intent(this, VerReportesOnlineActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(it);
        finish();
    }
}
