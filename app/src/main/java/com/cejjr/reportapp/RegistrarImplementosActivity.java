package com.cejjr.reportapp;

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

import mundo.Guardia;

public class RegistrarImplementosActivity extends AppCompatActivity implements OnClickListener {
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
            String tipoElemento= new String[]{"Linterna","Radio","Acompañante"}[(int)(Math.random()*3)];
            String elemento = scanContent+":"+tipoElemento+":Prestado:"+ Guardia.darGuardia().getIdGuardia()+":Linterna de maximo alcance, cuando es activada lanza rayos laser";//llamado al metodo de busqueda de implementos.
            agregarElementoScaneado(elemento);
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

        switch (propiedades[1])
        {
            case "Linterna":
                txtLinterna = (TextView)findViewById(R.id.txtLinterna);
                txtLinterna.setText(txtLinterna.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]);
                break;
            case "Radio":
                txtRadio = (TextView)findViewById(R.id.txtRadio);
                txtRadio.setText(txtRadio.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]);
                break;
            case "Acompañante":
                txtAcompañante = (TextView)findViewById(R.id.txtAcompañante);
                txtAcompañante.setText(txtAcompañante.getText()+"\n"+ propiedades[0]+" - "+propiedades[4]);
                break;
        }
    }
}
