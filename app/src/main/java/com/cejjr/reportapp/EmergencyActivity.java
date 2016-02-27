package com.cejjr.reportapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

    }

    public void llamar(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String tel = "tel:";
        switch (view.getId()) {
            case R.id.btnSupervisor:
                tel += "3132828108";//JJ - npi
                break;
            case R.id.btnAmbulancia:
                tel += "3213987247";//Arango - 125
                break;
            case R.id.btnBomberos:
                tel += "3043272857";//Niggalleta - 119
                break;
            case R.id.btnPolicia:
                tel += "3165397028";//Baby - 112
                break;
        }

        callIntent.setData(Uri.parse(tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            startActivity(callIntent);
            return;
        }

    }
}
