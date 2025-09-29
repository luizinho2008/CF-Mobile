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

public class Angelman extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_angelman);
    }

    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "intelectuais";
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://angelmanbrasil.org.br/sobre/";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.youtube.com/watch?v=FZaZ67QcB70";
        } else if (id == R.id.btnMaterial3) {
            url = "https://draanagabrielascuoghi.com.br/sindrome-de-angelman/";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}