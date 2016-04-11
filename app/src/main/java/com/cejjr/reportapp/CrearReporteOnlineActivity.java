package com.cejjr.reportapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CrearReporteOnlineActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private ImageButton capturar, seleccionar;
    private String outputFile,ide;
    private final int REQ_CODE_SPEECH_INPUT = 100;

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

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setTextSize(20);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

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
}
