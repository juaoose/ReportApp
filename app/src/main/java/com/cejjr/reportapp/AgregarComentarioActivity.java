package com.cejjr.reportapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import mundo.Guardia;
import mundo.RestClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AgregarComentarioActivity extends Activity {
    String BASE_URL = "http://ujkka6078b18.juanjorogo.koding.io:3000";
    private EditText edtxtInputText;
    private Button btnSpeak, btnSubmit;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Spinner spinnerCedulas;
    Handler mHandler;


    private void updateSpinner(ArrayList<String> arc){
        spinnerCedulas = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lista_item, R.id.label, arc);
        spinnerCedulas.setAdapter(adapter);
        spinnerCedulas.setSelection(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comentario);
        edtxtInputText = (EditText) findViewById(R.id.editText);
        btnSpeak = (Button) findViewById(R.id.btnSpeakComen);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnSubmit = (Button) findViewById(R.id.buttonSave);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String guard = String.valueOf(Guardia.darGuardia().getIdGuardia());
                    RestClient test = new RestClient();
                    test.submitComment(guard, spinnerCedulas.getSelectedItem().toString(), edtxtInputText.getText().toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                onBackPressed();
            }
        });



        //Download cedulas
        mHandler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(BASE_URL+"/guardiasList")
                .build();
        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray guardias = new JSONArray(response.body().string());
                    final ArrayList<String> guardiasTexto = new ArrayList<String>();

                    for (int i = 0; i < guardias.length(); i++) {
                        JSONObject js = guardias.getJSONObject(i);
                        String id = js.getString("cedula");
                        guardiasTexto.add(id);
                    }
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            updateSpinner(guardiasTexto);
                        }
                    });


                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
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
                    edtxtInputText.setText(result.get(0));
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
}
