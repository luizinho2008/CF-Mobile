package com.example.cf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TelaFisica extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_fisica); // Confirme que o nome está certo e bate com o XML
    }
    public void abrirAmputacao(View view) {
//        startActivity(new Intent(this, AmputacaoActivity.class));
    }

    public void abrirNanismo(View view) {
//        startActivity(new Intent(this, NanismoActivity.class));
    }

    public void abrirPlegia(View view) {
//        startActivity(new Intent(this, PlegiaActivity.class));
    }

    public void abrirPoliomielite(View view) {
//        startActivity(new Intent(this, PoliomieliteActivity.class));
    }
}