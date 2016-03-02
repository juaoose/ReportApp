package com.cejjr.reportapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import java.text.SimpleDateFormat;

import mundo.ReportApp;
import mundo.Reporte;

public class CrearReporteActivity extends AppCompatActivity {

    private String ide, asunto, outputFile;
    private boolean recordingStatus = false;

    Button audio, play, capturar, seleccionar, guardar;

    private MediaRecorder grabador;
    private Reporte reporte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reporte);

        //Botones
        audio = (Button) findViewById(R.id.btnRecord);
        play = (Button) findViewById(R.id.btnListen);
        capturar = (Button) findViewById(R.id.btnCapturar);
        seleccionar = (Button) findViewById(R.id.btnSelect);
        guardar = (Button) findViewById(R.id.btnSave);

        //Estado botones
        play.setEnabled(false);

        //Crear reporte
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
        ide = format.format(currentDate);
        outputFile = Environment.getExternalStorageDirectory()+"/ReportApp/"+ide;

        //mkdir
        File folder = new File(Environment.getExternalStorageDirectory() + "/ReportApp/"+ide);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }


        //onClick cada boton
        play.setOnClickListener(listenerEscuchar);
        audio.setOnClickListener(listenerGrabar);
        capturar.setOnClickListener(listenerCapturar);
        seleccionar.setOnClickListener(listenerSeleccion);
        guardar.setOnClickListener(listenerGuardar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crear_reporte, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1888){
            Toast.makeText(getApplicationContext(), "Foto guardada", Toast.LENGTH_LONG).show();
        }
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
     * Listener para grabar y detener la grabacion.
     */
    View.OnClickListener listenerGrabar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!recordingStatus) {
                recordingStatus = true;
                play.setEnabled(false);
                audio.setText("Detener");
                grabador = new MediaRecorder();
                grabador.setAudioSource(MediaRecorder.AudioSource.MIC);
                grabador.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                grabador.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                grabador.setOutputFile(outputFile+"/notaVoz.3gp");
                try {
                    grabador.prepare();
                    grabador.start();
                }catch (IOException e){
                    //TODO catch, log..
                }
            }
            else{
                recordingStatus = false;
                audio.setText("Grabar");
                grabador.stop();
                grabador.release();
                play.setEnabled(true);
            }

        }
    };

    /**
     * Listener para guardar el reporte en el mundo
     */
    View.OnClickListener listenerGuardar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reporte = new Reporte(ide);
            ReportApp.darInstancia().agregarReporte(reporte);
            onBackPressed();
        }
    };

    /**
     * Listener para reproducir la grabacion
     */
    View.OnClickListener listenerEscuchar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MediaPlayer m = new MediaPlayer();

            try {
                m.setDataSource(outputFile+"/notaVoz.3gp");
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                m.prepare();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            m.start();
            Toast.makeText(getApplicationContext(), "Reproduciendo nota", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Listener para tomar una foto.
     */
    View.OnClickListener listenerCapturar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Date currentDate = new Date();
                SimpleDateFormat fort = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
                String dateR = fort.format(currentDate);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(outputFile + "/img"+dateR+".jpg")));
                startActivityForResult(cameraIntent, 1888);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Listener para seleccionar imagenes
     */
    View.OnClickListener listenerSeleccion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent();
                //intent.setType("image/*");
                //intent.setType("image/jpeg");
                File nuevo = new File(outputFile+"/");
                Uri uri = Uri.fromFile(nuevo);

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(uri, "image/jpeg");
                startActivityForResult(Intent.createChooser(intent,"Seleccionar Imagenes"), 1);

//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                            + "/myFolder/");
//                    intent.setDataAndType(uri, "text/csv");
//                    startActivity(Intent.createChooser(intent, "Open folder"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onBackPressed() {
        if(reporte != null){
            super.onBackPressed();
        }else{
            File dir = new File(outputFile);
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }
            super.onBackPressed();
        }


    }
}
