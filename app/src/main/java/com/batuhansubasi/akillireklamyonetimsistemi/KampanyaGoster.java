package com.batuhansubasi.akillireklamyonetimsistemi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
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

public class KampanyaGoster extends AppCompatActivity {

    private TextView firmaAdi, kampanyaİcerik, kampanyaSuresi, kampanyaTuru, enlem, boylam;
    private List<Kampanyalar> kampanyalarList;
    private FirebaseFirestore dbe;
    private String deneme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kampanya_goster);

        firmaAdi       = (TextView)findViewById(R.id.ad_t);
        kampanyaİcerik = (TextView)findViewById(R.id.icerik_t);
        kampanyaSuresi = (TextView)findViewById(R.id.sure_t);
        kampanyaTuru   = (TextView)findViewById(R.id.tur_t);
        enlem          = (TextView)findViewById(R.id.enlem_t);
        boylam         = (TextView)findViewById(R.id.boylam_t);


        kampanyalarList = new ArrayList<>();

        String value;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getString("KONTROL");
        }

        for (String key: bundle.keySet())
        {
            Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();
            deneme = bundle.get(key).toString();
        }

            dbe = FirebaseFirestore.getInstance();
            dbe.collection("Builkkimlik").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot d : list) {
                                    Kampanyalar p = d.toObject(Kampanyalar.class);
                                        kampanyalarList.add(p);
                                        if (p.getFirmaAdi().equalsIgnoreCase(deneme)){
                                            firmaAdi.setText(p.getFirmaAdi());
                                            kampanyaİcerik.setText(p.getKampanyaIcerik());
                                            kampanyaSuresi.setText(p.getKampanyaSuresi());
                                            kampanyaTuru.setText(p.getKampanyaTuru());
                                            enlem.setText(p.getEnlem());
                                            boylam.setText(p.getBoylam());
                                        }
                                    }
                                }
                            }
                    });







        }

    }



