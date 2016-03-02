package com.cejjr.reportapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import mundo.ReportApp;

public class VerReportesActivity extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reportes);

        list = (ListView) findViewById(R.id.listaReportes);
        String[] reportes = ReportApp.darInstancia().darIdReportes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lista_item, R.id.label, reportes);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                // selected item
                String ident = ((TextView) view).getText().toString();

                // Launching new Activity on selecting single List Item
                //Intent i = new Intent(getApplicationContext(), InfoIngredienteActivity.class);
                // sending data to new activity
                //i.putExtra("reporte", id);
                //startActivity(i);
            }

        });
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
