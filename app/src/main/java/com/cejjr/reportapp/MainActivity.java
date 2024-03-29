package com.cejjr.reportapp;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import mundo.Guardia;
import mundo.RestClient;
import mundo.Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private BeaconManager beaconManager;

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
        EstimoteSDK.initialize(getApplicationContext(), "reportapp-05b", "8ad69240eb47fbf0807fee6c62a1a20d");
        beaconManager = new com.estimote.sdk.BeaconManager(getApplicationContext());
        beaconManager.connect(new com.estimote.sdk.BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "raged region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));
            }
        });
        beaconManager.setMonitoringListener(new com.estimote.sdk.BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Beacon j = list.get(0);
                String macAddress = j.getMacAddress() + "";
                verificarOcurrencia(macAddress, Guardia.darGuardia().getIdGuardia());

            }

            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
            }
        });
    }

    private void verificarOcurrencia(final String macAddress, final int idGuardia)
    {
        OkHttpClient client = new OkHttpClient();
        final Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(RestClient.BASE_URL+"/segGuar/123-123")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException
            {
                try {
                    long currentMillis = System.currentTimeMillis();
                    String entriess = response.body().string()
                            .replace("[","")
                            .replace("]","");
                    String[] entries = entriess.split("\\},\\{");


                    String ocurrence = entries[entries.length-1];
                    String pasoIntermedio = ocurrence.substring(0,ocurrence.lastIndexOf("v")-4);
                    long ultimaOcurrencia = Long.parseLong(pasoIntermedio.substring(pasoIntermedio.lastIndexOf("a")+3,pasoIntermedio.length()));
                    if((currentMillis-ultimaOcurrencia)>600000)
                    {
                        showNotification("Reportapp", "Hay una ubicación visitada, que falta por autorizar.", macAddress);
                    }
                    else
                    {
                        actualizarUltimaOcurrencia(macAddress, idGuardia, ultimaOcurrencia);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    private void actualizarUltimaOcurrencia(String macAddress, int idGuardia, long ultimaOcurrencia) {

    }

    private static class Gist {
        Map<String, GistFile> files;
    }

    private static class GistFile {
        String content;
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

    public void agregarComentario(View view) {
        Intent i = new Intent(this, AgregarComentarioActivity.class);
        startActivity(i);
    }

    public void emergencyCall(View view) {
        Intent i = new Intent(this, EmergencyActivity.class);
        startActivity(i);
    }

    public void crearReporte(View view) {
        Util ins = Util.darInstancia();
        if (ins.tieneWiFi(getApplicationContext()) || ins.tieneRed(getApplicationContext())) {
            Intent i = new Intent(this, CrearReporteOnlineActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, CrearReporteActivity.class);
            startActivity(i);
        }
    }

    public void registrarImplementos(View view) {
        Intent i = new Intent(this, RegistrarImplementosActivity.class);
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
        Util ins = Util.darInstancia();
        if (ins.tieneWiFi(getApplicationContext()) || ins.tieneRed(getApplicationContext())) {
            Intent i = new Intent(this, VerReportesOnlineActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, VerReportesActivity.class);
            startActivity(i);
        }
    }

    public void showNotification(String title, String message, String macAddress) {
        Intent notifyIntent = new Intent(this, AutorizarBeaconActivity.class);
        notifyIntent.putExtra("macAddress", macAddress);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
