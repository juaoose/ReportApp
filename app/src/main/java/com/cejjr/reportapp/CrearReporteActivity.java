package com.cejjr.reportapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import java.text.SimpleDateFormat;

import mundo.Guardia;
import mundo.ReportApp;
import mundo.Reporte;

public class CrearReporteActivity extends AppCompatActivity {
    /**
     * String con:
     * ide: identificador del reporte
     * asunto: asunto del reporte
     * outputFile: path donde esta el reporte
     */
    private String ide, asunto, outputFile;

    /**
     * Boolean utilizado para modificar el estado de los botones de grabacion y reproduccion.
     */
    private boolean recordingStatus = false;

    /**
     * Botones de esta vista.
     */
    Button audio, play, capturar, seleccionar, guardar;

    /**
     * MediaRecorder
     */
    private MediaRecorder grabador;

    /**
     * El reporte que se creara, guardara o descartara
     */
    private Reporte reporte;

    /**
     * Metodo que se ejecuta al crear la vista.
     * Se definen los botones, su estado inicial y el identificador del reporte.
     * Se verifica el estado del folder donde se guardan los reportes.
     * @param savedInstanceState
     */
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
        if(Guardia.darGuardia().mostrarRecomendacion())
        {
            final Dialog dialog = new Dialog(CrearReporteActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);

            // set the custom dialog components - text, image and button
            CheckBox ckb = (CheckBox) dialog.findViewById(R.id.checkBox);
            final boolean showAgain = ckb.isChecked();

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Guardia.darGuardia().recordarMensaje(!showAgain);
                    dialog.dismiss();

                }
            });

            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crear_reporte, menu);
        return true;
    }

    /**
     * FileUtils para obtener el Path desde un URI para las imagenes.
     * Hay problemas con la galería defecto de mi celular.
     * @param uri
     * @return
     */
    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    /**
     * Metodo que permite copiar un archivo.
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    /**
     * Metodo que escucha los intents que se finalizan y poseen informacion, se ppuede verificar su request code y el codigo de estado.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Camera
        if(requestCode == 1888){
            Toast.makeText(getApplicationContext(), "Foto guardada", Toast.LENGTH_LONG).show();
        }
        //Chooser
        else if(requestCode == 1){

            String selectedImagePath = getAbsolutePath(data.getData());

            //copy with proper name
            Date currentDate = new Date();
            SimpleDateFormat fort = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
            String dateR = fort.format(currentDate);
            File src = new File(selectedImagePath);
            File dest = new File(outputFile+"/img"+dateR+".jpg");
            try {
                copy(src, dest);
            }catch(IOException e){

            }

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
                //cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                //        Uri.fromFile(new File(outputFile + "/img"+dateR+".jpg")));
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

    /**
     * Metodo que se llama al presionar el boton atras de los dispositivos android.
     */
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
