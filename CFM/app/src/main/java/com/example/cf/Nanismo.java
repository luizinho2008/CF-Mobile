package com.example.cf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Nanismo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nanismo);
    }

    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "fisicas";
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://www.gov.br/mdh/pt-br/navegue-por-temas/crianca-e-adolescente/acoes-e-programas/PESSOASCOMNANISMOESEUSDIREITOS.pdf";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.instagram.com/annabra_nanismo/";
        } else if (id == R.id.btnMaterial3) {
            url = "https://www.gov.br/mdh/pt-br/navegue-por-temas/crianca-e-adolescente/acoes-e-programas/AMBIENTESACESSVEISEAPESSOACOMNANISMO.pdf";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}