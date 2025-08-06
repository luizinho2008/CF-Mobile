package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TelaInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
    }

    public void fisicas(View v) {
        Toast.makeText(this, "Indo pra tela físicas", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TelaInicial.this, TelaFisica.class);
        startActivity(intent);
    }
    public void intelectuais(View v) {
        Toast.makeText(this, "Indo pra tela intelectuais", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TelaInicial.this, TelaIntelectual.class);
        startActivity(intent);
    }
    public void neurodivergentes(View v) {
        Toast.makeText(this, "Indo pra tela neurodivergentes", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TelaInicial.this, TelaNeurodivergentes.class);
        startActivity(intent);
    }
    public void sensoriais(View v) {
        Toast.makeText(this, "Indo pra tela sensoriais", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TelaInicial.this, TelaSensorial.class);
        startActivity(intent);
    }

//    public void chatbot(View v) {
//        Intent intent = new Intent(TelaInicial.this, ChatBot.class);
//        startActivity(intent);
//    }
}