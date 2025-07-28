package com.example.cf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TelaFisica extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_fisica); // Confirme que o nome está certo e bate com o XML
    }
    public void abrirAmputacao(View view) {
        startActivity(new Intent(this, Amputacao.class));
    }

    public void abrirNanismo(View view) {
        startActivity(new Intent(this, Nanismo.class));
    }

    public void abrirPlegia(View view) {
        startActivity(new Intent(this, Plegia.class));
    }

    public void abrirPoliomielite(View view) {
        startActivity(new Intent(this, Poliomielite.class));
    }
}