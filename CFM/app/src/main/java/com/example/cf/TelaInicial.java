package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;
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
    }
    public void intelectuais(View v) {
        Toast.makeText(this, "Indo pra tela intelectuais", Toast.LENGTH_SHORT).show();
    }
    public void neurodivergentes(View v) {
        Toast.makeText(this, "Indo pra tela neurodivergentes", Toast.LENGTH_SHORT).show();
    }
    public void sensoriais(View v) {
        Toast.makeText(this, "Indo pra tela sensoriais", Toast.LENGTH_SHORT).show();
    }
}