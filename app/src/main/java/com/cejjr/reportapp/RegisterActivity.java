package com.cejjr.reportapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mundo.Guardia;

public class RegisterActivity extends AppCompatActivity {

    static final int PICK_CONTACT=1;

    private String cNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etxt = (EditText) findViewById(R.id.etxtId);
        etxt.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
    }

    public void selectSupervisor(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                        }
                    }
                }
                break;
        }
    }

    public void iniciarTurno(View v)
    {   if( ((EditText)findViewById(R.id.etxtId)).getText().toString().matches("")){
            Toast.makeText(getApplicationContext(), "Ingresar su identificador", Toast.LENGTH_LONG).show();
        }else{

        int idGuardia = Integer.parseInt(((EditText)findViewById(R.id.etxtId)).getText().toString().trim().replace(" ", ""));
        Guardia.darGuardia().inicializar(cNumber,idGuardia);
        Intent s = new Intent(this,MainActivity.class);
        startActivity(s);
    }

    }
}
