package com.example.cf;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TelaSensorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sensorial); // Confirme que o nome está certo e bate com o XML
    }

//    public void abrirCegueira(View view) {
//        startActivity(new Intent(this, AmputacaoActivity.class));
//    }
//
//    public void abrirSurdez(View view) {
//        startActivity(new Intent(this, NanismoActivity.class));
//    }

}
