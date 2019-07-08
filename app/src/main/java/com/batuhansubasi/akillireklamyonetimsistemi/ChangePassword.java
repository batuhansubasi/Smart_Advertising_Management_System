package com.batuhansubasi.akillireklamyonetimsistemi;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener{

    private Button DegistirButton;
    private EditText changeName, changePassword, changePassword2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    String kullaniciAdi, sifre, yeniSifre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        changeName      = (EditText) findViewById(R.id.changeName);
        changePassword  = (EditText) findViewById(R.id.changePassword);
        changePassword2 = (EditText) findViewById(R.id.changePassword2);
        DegistirButton  = (Button)   findViewById(R.id.button4);

        DegistirButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view == DegistirButton){
            degistirSifre();
        }
    }

    private void degistirSifre() {
        kullaniciAdi = changeName.getText().toString().trim();
        sifre = changePassword.getText().toString().trim();
        yeniSifre = changePassword2.getText().toString().trim();

        if(TextUtils.isEmpty(kullaniciAdi)){
            Toast.makeText(getApplicationContext(), "Kullanıcı Adı Boş!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(sifre) || TextUtils.isEmpty(yeniSifre)){
            Toast.makeText(getApplicationContext(), "Sifre Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(sifre.length() < 6 || yeniSifre.length() < 6){
            Toast.makeText(getApplicationContext(), "Sifre 6 karakterden az olamaz!...", Toast.LENGTH_LONG).show();
            return;
        }

        kullaniciAdi = kullaniciAdi + "@hotmail.com";

        firebaseAuth.signInWithEmailAndPassword(kullaniciAdi,sifre).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){   //Düzgün bir sekilde giriş yapıldıysa...

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Giris yapılan kullanıcı alındı...
                    progressDialog.setMessage("Degistiriliyor");
                    progressDialog.show();

                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){  //Kullanıcı Silme İşlemi Düzgünse...

                                firebaseAuth.createUserWithEmailAndPassword(kullaniciAdi,yeniSifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {    //Yeni kullanıcı yarat...
                                        Toast.makeText(getApplicationContext(), "Sifre Degistirme Basarili!...", Toast.LENGTH_SHORT).show();
                                        progressDialog.hide();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Güncel sifreyi yanlis girdiniz!...", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });
    }


}
