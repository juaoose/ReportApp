package com.cejjr.reportapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import mundo.ReportApp;
import mundo.RestClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class VerReportesOnlineActivity extends AppCompatActivity {
    String BASE_URL = "http://ujkka6078b18.juanjorogo.koding.io:3000";
    private ListView list;
    private JSONArray reportes;
    Handler mHandler;
    ArrayList<String> reportesText = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reportes_online);

        list = (ListView) findViewById(R.id.listaReportesO);



        mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

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
                    final ArrayList<String> reportesTexto = new ArrayList<String>();

                    for (int i = 0; i < reportes.length(); i++) {
                        JSONObject js = reportes.getJSONObject(i);
                        String id = js.getString("identificador");
                        reportesTexto.add(id);
                    }

                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            reportesText = reportesTexto;
                            adapter(reportesTexto);
                        }
                    });


                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                // selected item
                String ident = ((TextView) view).getText().toString();
                Intent i = new Intent(getApplicationContext(), VerDetallesOnline.class);
                i.putExtra("reporte", ident);
                startActivity(i);
            }

        });
    }

    public void adapter(ArrayList<String> arc){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lista_item, R.id.label, arc);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_reportes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
