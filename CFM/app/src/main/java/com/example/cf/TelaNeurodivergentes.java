package com.example.cf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TelaNeurodivergentes extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_neurodivergentes);
    }

    public void abrirTDAH(View view) {
        startActivity(new Intent(this, TDAH.class));
    }

    public void abrirAutismo(View view) {
        startActivity(new Intent(this, Autismo.class));
    }

    public void abrirDislexia(View view) {
        startActivity(new Intent(this, Dislexia.class));
    }
}
