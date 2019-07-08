package com.batuhansubasi.akillireklamyonetimsistemi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button kayitButton, sifreDegistirButton, girisButton;
    private EditText loginName, loginPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        kayitButton = (Button) findViewById(R.id.button2);
        kayitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityRegister();
            }
        });

        sifreDegistirButton = (Button)findViewById(R.id.button3);
        sifreDegistirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityChangePassword();
            }
        });

        loginName = (EditText)findViewById(R.id.loginName);
        loginPassword = (EditText)findViewById(R.id.loginPassword);

        girisButton = (Button)findViewById(R.id.btnLogin);
        girisButton.setOnClickListener(this);

    }

    public void openActivityRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openActivityChangePassword(){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    public void deneme(){
        Intent intent = new Intent(this, KonumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view == girisButton){
            userLogin();
        }
    }

    private void userLogin() {
        String kullaniciAdi = loginName.getText().toString().trim();
        String sifre = loginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(kullaniciAdi)){
            Toast.makeText(getApplicationContext(), "Kullanici Adi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(sifre)){
            Toast.makeText(getApplicationContext(), "Sifre Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(sifre.length() < 6){
            Toast.makeText(getApplicationContext(), "Sifre 6 karakterden az olamaz!...", Toast.LENGTH_LONG).show();
            return;
        }

        kullaniciAdi = kullaniciAdi + "@hotmail.com";
        progressDialog.setMessage("Giris Yapiliyor...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(kullaniciAdi,sifre).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Giris Basarili!...", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    deneme();
                } else {
                    Toast.makeText(getApplicationContext(), "Giris Yapilamadi!...", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });

    }

}