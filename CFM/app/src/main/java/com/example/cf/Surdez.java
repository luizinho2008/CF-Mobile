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

public class Surdez extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_surdez);
    }

    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "sensoriais";
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://bvsms.saude.gov.br/10-11-dia-nacional-de-prevencao-e-combate-a-surdez-3/#:~:text=Surdez%20%C3%A9%20a%20diminui%C3%A7%C3%A3o%20da,mundo%20sofrem%20alguma%20perda%20auditiva.";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.tuasaude.com/causas-da-surdez/";
        } else if (id == R.id.btnMaterial3) {
            url = "https://bvsms.saude.gov.br/surdez-3/";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}