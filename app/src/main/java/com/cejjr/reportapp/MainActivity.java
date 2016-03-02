package com.cejjr.reportapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

import mundo.Guardia;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mkdir

        File folder = new File(Environment.getExternalStorageDirectory() + "/ReportApp");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
        ((TextView)findViewById(R.id.textView2)).setText(Guardia.darGuardia().darNumeroSupervisor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void emergencyCall(View view)
    {
        Intent i = new Intent(this,EmergencyActivity.class);
        startActivity(i);
    }

    public void crearReporte(View view){
        Intent i = new Intent(this, CrearReporteActivity.class);
        startActivity(i);
    }

    public void verReportes(View view){
        Intent i = new Intent(this, VerReportesActivity.class);
        startActivity(i);
    }
}
