package com.cejjr.reportapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import mundo.ReportApp;

public class VerDetalleReporteActivity extends AppCompatActivity {
    Button playBtn;
    private String outputFile;
    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<String> nombres = new ArrayList<String>();
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalle_reporte);

        playBtn = (Button) findViewById(R.id.btnAudio);
        playBtn.setOnClickListener(listenerEscuchar);



        Intent i = getIntent();
        String nombre = i.getStringExtra("reporte");
        //Ingrediente ing= RecetasEfectivas.darInstancia().darIngrediente(nombre);
        outputFile = Environment.getExternalStorageDirectory()+"/ReportApp/"+nombre;

        File fileVoz = new File(outputFile + "/notaVoz.3gp");
        if(!fileVoz.exists()){
            playBtn.setText("No hay grabacion");
            playBtn.setEnabled(false);
        }


        File file=new File(outputFile);
        File[] lista = file.listFiles();
        int count = 0;
        for (File f: lista){
            String location = f.getPath();
            String name = f.getName();
            if (name.endsWith(".jpg") ) {
                count++;
                paths.add(location);
                nombres.add("Evidencia "+count);
            }

        }

        //lv

        list = (ListView) findViewById(R.id.listDet);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lista_item, R.id.label, nombres);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                // selected item
                //String ident = ((TextView) view).getText().toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://"+paths.get(position)), "image/*");
                startActivity(intent);
            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_detalle_reporte, menu);
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

    /**
     * Listener para reproducir la grabacion
     */
                        View.OnClickListener listenerEscuchar = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaPlayer m = new MediaPlayer();

                                try {
                                    m.setDataSource(outputFile + "/notaVoz.3gp");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    m.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                m.start();
                                Toast.makeText(getApplicationContext(), "Reproduciendo nota", Toast.LENGTH_LONG).show();
                            }
    };
}
