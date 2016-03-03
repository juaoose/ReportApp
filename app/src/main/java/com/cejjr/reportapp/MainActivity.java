package com.cejjr.reportapp;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import mundo.Guardia;

public class MainActivity extends AppCompatActivity {
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    public final static String TAG_EVENTO = "Reportapp Event";

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    Map<String, String> map = new HashMap<String, String>();
    String[] eventos = {};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map.put("A", "13;30;A1:23;15;A4");
        map.put("B", "15;45;B4:20;20;A6");
        map.put("C", "18;32;C3:22;16;C5");
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

    public void emergencyCall(View view) {
        Intent i = new Intent(this, EmergencyActivity.class);
        startActivity(i);
    }

    public void crearReporte(View view) {
        Log.d("", "holo");
        Intent i = new Intent(this, CrearReporteActivity.class);
        startActivity(i);
    }

    public void addCalendarEvents(View view) {
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        System.out.println(cr.toString());
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String gmail = null;
        int contador = 0;
        while (contador < list.length && gmail == null) {
            if (list[contador].type.equalsIgnoreCase("com.google")) {
                gmail = list[contador].name;
                break;
            }
            contador++;
        }
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{gmail, "com.google",
                gmail};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            System.out.println(uri);
            System.out.println(EVENT_PROJECTION);
            System.out.println(selection);
            System.out.println(selectionArgs[0] + selectionArgs[1] + selectionArgs[2]);
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }
        cur.moveToFirst();
        final long calID = cur.getLong(PROJECTION_ID_INDEX);
        System.out.println("" + calID);
        //eliminarEventos();

        final String[] arreglo = {"A", "B", "C"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        System.out.println(builder.toString());
        builder.setTitle("Seleccione perfil")
                .setItems(arreglo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getEventosPorArchivo(arreglo[which], calID);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarEventos() {

    }

    public void getEventosPorArchivo(String archivo, Long calID) {
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String resultado = map.get(archivo);
        eventos = resultado.split(":");
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        System.out.println(beginTime);
        System.out.println(endTime);
        cr = getContentResolver();
        ContentValues values = new ContentValues();

        for (String evento : eventos) {
            String[] detalle = evento.split(";");
            beginTime.set(Calendar.HOUR, Integer.parseInt(detalle[0]));
            beginTime.set(Calendar.MINUTE, Integer.parseInt(detalle[1]));
            startMillis = beginTime.getTimeInMillis();
            endTime.set(Calendar.HOUR, Integer.parseInt(detalle[0]));
            endTime.set(Calendar.MINUTE, Integer.parseInt(detalle[1]));
            endTime.add(Calendar.MINUTE, 15);
            endMillis = endTime.getTimeInMillis();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Visitar " + detalle[2]);
            values.put(CalendarContract.Events.DESCRIPTION, TAG_EVENTO);
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            TimeZone tz = TimeZone.getDefault();
            values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            }
        }
    }

    public void verReportes(View view) {
        Intent i = new Intent(this, VerReportesActivity.class);
        startActivity(i);
    }
}
