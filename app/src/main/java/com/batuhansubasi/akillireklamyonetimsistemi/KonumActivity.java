package com.batuhansubasi.akillireklamyonetimsistemi;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KonumActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private RadioGroup  rg1;
    private RadioButton otokonum, manuelkonum, rb;
    private EditText    boylam, enlem, uzaklik, kullanicininGirdigiMagazaAdi;
    private Button      kaydet;
    private ListView    list;

    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseFirestore db;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private List<Kampanyalar> kampanyalarList;

    private String secilen, boylams, enlems, uzakliks, magAdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konum);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        kampanyalarList = new ArrayList<>();


        rg1         = (RadioGroup)  findViewById(R.id.radioGroup);
        otokonum    = (RadioButton) findViewById(R.id.radioButton3);
        manuelkonum = (RadioButton) findViewById(R.id.radioButton4);
        boylam      = (EditText)    findViewById(R.id.editText2);
        enlem       = (EditText)    findViewById(R.id.editText3);
        uzaklik     = (EditText)    findViewById(R.id.editText);
        kaydet      = (Button)      findViewById(R.id.savebutton);
        list        = (ListView)    findViewById(R.id.listView);
        kullanicininGirdigiMagazaAdi = (EditText) findViewById(R.id.editText4);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                secilen = (String) (list.getItemAtPosition(position));
            }
        });

        //Kategori Kısmını db' den alip listbox' u doldurmak icin...
        db = FirebaseFirestore.getInstance();
        db.collection("Builkkimlik").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            boolean kontrol = false;
                            String  temp = null;

                            for (DocumentSnapshot d : list) {
                                Kampanyalar p = d.toObject(Kampanyalar.class);

                                kontrol = false;
                                for (int j = 0 ; j < arrayList.size(); j++){
                                    temp = arrayList.get(j);
                                    if (temp.equalsIgnoreCase(p.getKampanyaTuru())){
                                        kontrol = true;
                                    }
                                }
                                if(kontrol == false) {
                                    arrayList.add(p.getKampanyaTuru());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }  else {
                            Toast.makeText(getApplicationContext(), "Kategori Veri Tabanında Boş!...", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        kaydet.setOnClickListener(this);
    }

    public void rbclick(View v){
        int radiobuttonid = rg1.getCheckedRadioButtonId();
        rb = findViewById(radiobuttonid);

        if(rb.getText().equals("Otomatik Konum")){
            fetchlocation();
        } else {
            boylam.setText("");
            enlem.setText("");
        }
    }

    private void fetchlocation() {
        if (ContextCompat.checkSelfPermission(KonumActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(KonumActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Konum Izni Zorunlu")
                        .setMessage("Konum icin izin vermeniz gerekiyor")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(KonumActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        }).setNegativeButton("NOT OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            } else {
                ActivityCompat.requestPermissions(KonumActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Double latittude1 = location.getLatitude();
                                String lat1 = Double.toString(latittude1);

                                Double longttidue1 = location.getLongitude();
                                String lon1 = Double.toString(longttidue1);

                                enlem.setText(lat1);
                                boylam.setText(lon1);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }

    }


    @Override
    public void onClick(View view) {
        if (view == kaydet){
            kaydet();
        }
    }

    private void kaydet() {
        boylams  = boylam.getText().toString().trim();
        enlems   = enlem.getText().toString().trim();
        uzakliks = uzaklik.getText().toString().trim();
        magAdi   = kullanicininGirdigiMagazaAdi.getText().toString().trim();

        if(TextUtils.isEmpty(boylams)){
            Toast.makeText(getApplicationContext(), "Boylam Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(enlems)){
            Toast.makeText(getApplicationContext(), "Enlem Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(uzakliks)){
            Toast.makeText(getApplicationContext(), "Uzaklik Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }


        if(!TextUtils.isEmpty(magAdi) && !TextUtils.isEmpty(secilen)){
            Toast.makeText(getApplicationContext(), "Hem mağaza ismi, hem de tür secemezsiniz.", Toast.LENGTH_LONG).show();
            kullanicininGirdigiMagazaAdi.setText("");
            return;
        }

        /*
        if(Integer.parseInt(uzakliks)>100){
            Toast.makeText(getApplicationContext(), "100 metreden fazla giremezsiniz!...", Toast.LENGTH_LONG).show();
            return;
        }*/

        ///-----------------------KONTROLLER BİTİŞ-----------------------------------------------------------------------------------///

        if (TextUtils.isEmpty(magAdi) && TextUtils.isEmpty(secilen)) {  //Magaza Türü ve Mağaza Adı Seçilmemiş ise
            db = FirebaseFirestore.getInstance();
            db.collection("Builkkimlik").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                //all data (magaza adı ve turu girilmezse)
                                for (DocumentSnapshot d : list) {
                                    Kampanyalar p = d.toObject(Kampanyalar.class);

                                    //islemler baslangic
                                    String magazaenlem = p.getEnlem();
                                    String magazaboylam = p.getBoylam();

                                    float[] results = new float[1];
                                    Location.distanceBetween(Double.parseDouble(magazaenlem), Double.parseDouble(magazaboylam),
                                            Double.parseDouble(enlems), Double.parseDouble(boylams), results);
                                    float distance = results[0];

                                    if (distance < Double.parseDouble(uzakliks)) {
                                        kampanyalarList.add(p);

                                        Intent intent = new Intent(KonumActivity.this, KampanyaGoster.class);
                                        intent.putExtra("KONTROL",p.getFirmaAdi());
                                        PendingIntent pendingIntent = PendingIntent.getActivity(KonumActivity.this,kampanyalarList.size(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(KonumActivity.this)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("Firma İsmi: "+p.getFirmaAdi())
                                                .setContentIntent(pendingIntent)
                                                .setSmallIcon(R.drawable.common_full_open_on_phone)
                                                .setAutoCancel(true)
                                                .setContentText("Kamp.Turu: "+p.getKampanyaTuru()+" Kamp.İcerik: "+p.getKampanyaIcerik());

                                        NotificationManager nm = (NotificationManager) KonumActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        nm.notify(kampanyalarList.size(), builder.build());
                                    }
                                }

                                Toast.makeText(getApplicationContext(), "Aramalarınıza göre " + kampanyalarList.size() + " tane mağaza bulundu...", Toast.LENGTH_LONG).show();

//                                File file = new File(KonumActivity.this.getFilesDir(), "file");
//                                ObjectOutputStream fileOut = null;
//                                try {
//                                    fileOut = new ObjectOutputStream(new FileOutputStream("file"));
//                                    fileOut.writeObject(kampanyalarList);
//                                    fileOut.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "Veri Tabanı Bos!...", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
        }





        if (TextUtils.isEmpty(magAdi) && !TextUtils.isEmpty(secilen)) {  //Magaza Türü SEÇİLMİŞSE, Mağaza Adı Seçilmemişse
            db = FirebaseFirestore.getInstance();
            db.collection("Builkkimlik").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                //all data (magaza adı ve turu girilmezse)
                                for (DocumentSnapshot d : list) {
                                    Kampanyalar p = d.toObject(Kampanyalar.class);

                                    //islemler baslangic
                                    String magazaenlem = p.getEnlem();
                                    String magazaboylam = p.getBoylam();
                                    String magazaTurYeni = p.getKampanyaTuru();
                                    magazaTurYeni.toLowerCase();
                                    secilen.toLowerCase();

                                    float[] results = new float[1];
                                    Location.distanceBetween(Double.parseDouble(magazaenlem), Double.parseDouble(magazaboylam),
                                            Double.parseDouble(enlems), Double.parseDouble(boylams), results);
                                    float distance = results[0];

                                    if (distance < Double.parseDouble(uzakliks) && magazaTurYeni.equalsIgnoreCase(secilen)) {
                                        kampanyalarList.add(p);

                                        Intent intent = new Intent(KonumActivity.this, KampanyaGoster.class);
                                        intent.putExtra("KONTROL",kampanyalarList.size());
                                        PendingIntent pendingIntent = PendingIntent.getActivity(KonumActivity.this,kampanyalarList.size(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(KonumActivity.this)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("Firma İsmi: "+p.getFirmaAdi())
                                                .setContentIntent(pendingIntent)
                                                .setSmallIcon(R.drawable.common_full_open_on_phone)
                                                .setAutoCancel(true)
                                                .setContentText("Kamp.Turu: "+p.getKampanyaTuru()+" Kamp.İcerik: "+p.getKampanyaIcerik());

                                        NotificationManager nm = (NotificationManager) KonumActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        nm.notify(kampanyalarList.size(), builder.build());
                                    }
                                }

                                Toast.makeText(getApplicationContext(), "Aramalarınıza göre " + kampanyalarList.size() + " tane mağaza bulundu...", Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(getApplicationContext(), "Veri Tabanı Bos!...", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
        }




        if (!TextUtils.isEmpty(magAdi) && TextUtils.isEmpty(secilen)) {  //Magaza Türü Seçilmemiş, Mağaza Adı SEÇİLMİŞSE
            db = FirebaseFirestore.getInstance();
            db.collection("Builkkimlik").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                //all data (magaza adı ve turu girilmezse)
                                for (DocumentSnapshot d : list) {
                                    Kampanyalar p = d.toObject(Kampanyalar.class);

                                    //islemler baslangic
                                    String magazaenlem = p.getEnlem();
                                    String magazaboylam = p.getBoylam();
                                    String magAdiYeni = p.getFirmaAdi();

                                    magAdiYeni.toLowerCase();
                                    magAdi.toLowerCase();

                                    float[] results = new float[1];
                                    Location.distanceBetween(Double.parseDouble(magazaenlem), Double.parseDouble(magazaboylam),
                                            Double.parseDouble(enlems), Double.parseDouble(boylams), results);
                                    float distance = results[0];

                                    if (distance < Double.parseDouble(uzakliks) && magAdiYeni.contains(magAdi)) {
                                        kampanyalarList.add(p);

                                        Intent intent = new Intent(KonumActivity.this, KampanyaGoster.class);
                                        intent.putExtra("KONTROL",kampanyalarList.size());
                                        PendingIntent pendingIntent = PendingIntent.getActivity(KonumActivity.this,kampanyalarList.size(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(KonumActivity.this)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("Firma İsmi: "+p.getFirmaAdi())
                                                .setContentIntent(pendingIntent)
                                                .setSmallIcon(R.drawable.common_full_open_on_phone)
                                                .setAutoCancel(true)
                                                .setContentText("Kamp.Turu: "+p.getKampanyaTuru()+" Kamp.İcerik: "+p.getKampanyaIcerik());

                                        NotificationManager nm = (NotificationManager) KonumActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        nm.notify(kampanyalarList.size(), builder.build());
                                    }
                                }

                                Toast.makeText(getApplicationContext(), "Aramalarınıza göre " + kampanyalarList.size() + " tane mağaza bulundu...", Toast.LENGTH_LONG).show();
                                if(kampanyalarList.size() > 0){

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Veri Tabanı Bos!...", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });
        }
    }
}
