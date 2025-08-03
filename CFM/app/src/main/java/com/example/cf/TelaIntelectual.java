package com.example.cf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TelaIntelectual extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_intelectual); // Confirme que o nome está certo e bate com o XML
    }

    public void abrirDown(View view) {
        startActivity(new Intent(this, Down.class));
    }

    public void abrirCri(View view) {
        startActivity(new Intent(this, Cri.class));
    }

    public void abrirAngelman(View view) {
        startActivity(new Intent(this, Angelman.class));
    }

    public void abrirWilliams(View view) {
        startActivity(new Intent(this, Williams.class));
    }

}
