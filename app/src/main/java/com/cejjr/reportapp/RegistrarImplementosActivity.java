package com.cejjr.reportapp;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import mundo.Guardia;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RegistrarImplementosActivity extends AppCompatActivity implements OnClickListener {
    String BASE_URL = "http://ujkka6078b18.juanjorogo.koding.io:3000";
    Handler mHandler;
    private Button scanBtn;
    private TextView txtAcompañante;
    private TextView txtRadio;
    private TextView txtLinterna;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_implementos);
        scanBtn = (Button)findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(this);
        txtAcompañante = ((TextView)findViewById(R.id.txtAcompañante));
        txtLinterna = ((TextView)findViewById(R.id.txtLinterna));
        txtRadio = ((TextView)findViewById(R.id.txtRadio));
        String linterna=null,
                radio=null,
                acompañante=null;
        if(savedInstanceState!=null)
        {
            linterna = savedInstanceState.getString("linterna");
            radio = savedInstanceState.getString("radio");
            acompañante = savedInstanceState.getString("acompañante");
        }
        if(linterna != null)
            txtLinterna.setText(linterna);
        if(radio != null)
            txtRadio.setText(radio);
        if(acompañante!=null)
            txtAcompañante.setText(acompañante);
    }
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putString("linterna", txtLinterna.getText().toString());
        outState.putString("acompañante", txtAcompañante.getText().toString());
        outState.putString("radio",  txtRadio.getText().toString());
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            //String tipoElemento= new String[]{"Linterna","Radio","Acompañante"}[(int)(Math.random()*3)];
            //String elemento = scanContent+":"+tipoElemento+":Prestado:"+ Guardia.darGuardia().getIdGuardia()+":Linterna de maximo alcance, cuando es activada lanza rayos laser";//llamado al metodo de busqueda de implementos.
            //agregarElementoScaneado(elemento);

            //metodos con servidor
            mHandler = new Handler(Looper.getMainLooper());
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(BASE_URL+"/implementos/"+scanContent)
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
                        final String tipoImplemento = reportes.getString("tipoImplemento");
                        final boolean comment = reportes.getBoolean("estado");
                        final String descripcion = reportes.getString("descripcion");
                        final String scanNum = reportes.getString("barcodeId");
                        final String elemento = scanNum+":"+tipoImplemento+":"+ !comment +":"+ Guardia.darGuardia().getIdGuardia()+":"+descripcion;
                        mHandler.post(new Runnable(){
                            @Override
                            public void run(){
                                //TODO
                                agregarElementoScaneado(elemento);
                            }
                        });


                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public void agregarElementoScaneado(String nuevoImplemento)
    {
        String[] propiedades = nuevoImplemento.split(":");
        String estado = "En uso";
        if(propiedades[2].equals("true")){
            estado = "En uso";
        }
        else{
            estado = "Libre";
        }

        switch (propiedades[1])
        {
            case "Linterna":
                txtLinterna = (TextView)findViewById(R.id.txtLinterna);
                txtLinterna.setText(txtLinterna.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]+" - "+ estado);
                break;
            case "Radio":
                txtRadio = (TextView)findViewById(R.id.txtRadio);
                txtRadio.setText(txtRadio.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]+" - "+ estado);
                break;
            case "Acompañante":
                txtAcompañante = (TextView)findViewById(R.id.txtAcompañante);
                txtAcompañante.setText(txtAcompañante.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]);
                break;
        }
    }
}
