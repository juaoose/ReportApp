package com.cejjr.reportapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mundo.ReportApp;
import mundo.Reporte;
import mundo.RestClient;

public class CrearReporteOnlineActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ImageButton capturar, seleccionar, guardar;
    private String outputFile,ide;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static String BASE_URL = "http://157.253.207.245:3000/reportes";
    private Reporte reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reporte_online);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        capturar = (ImageButton) findViewById(R.id.btnCapture);
        capturar.setOnClickListener(listenerCapturar);
        seleccionar = (ImageButton) findViewById(R.id.btnGallery);
        seleccionar.setOnClickListener(listenerSeleccion);
        guardar = (ImageButton) findViewById(R.id.btnSaved);
        guardar.setOnClickListener(listenerGuardar);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        //Crear reporte
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
        ide = format.format(currentDate);
        outputFile = Environment.getExternalStorageDirectory()+"/ReportApp/"+ide;
        boolean success = true;
        File folder = new File(Environment.getExternalStorageDirectory() + "/ReportApp/"+ide);
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
        //sPEECH INPUT
        else if(requestCode == REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtSpeechInput.setTextSize(20);
                txtSpeechInput.setText(result.get(0));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     * Listener para guardar el reporte en el mundo
     */
    View.OnClickListener listenerGuardar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reporte = new Reporte(ide);
            ReportApp.darInstancia().agregarReporte(reporte);
            //Util para imagenes
            ArrayList<String> paths = new ArrayList<String>();
            File file=new File(outputFile);
            File[] lista = file.listFiles();
            int count = 0;
            for (File f: lista){
                String location = f.getPath();
                String name = f.getName();
                if (name.endsWith(".jpg") ) {
                    count++;
                    paths.add(location);
                }

            }
            //Comunicacion servidor rest.
            try {
                File picture = new File(paths.get(0));
                RestClient test = new RestClient();
                //test.get(BASE_URL);
                //test.post(BASE_URL,reporte, picture);
                //test.image(BASE_URL, picture);
                test.upload("http://157.253.207.245:3000/upload", reporte, picture);

            }
            catch (Exception e)
            {

            }
            onBackPressed();
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
}
