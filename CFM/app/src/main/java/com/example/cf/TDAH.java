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

public class TDAH extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tdah);
    }

    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "neurodivergentes";
    }

    public void chatbot(View v) {
        Informations.number = "2";
        Intent intent = new Intent(TDAH.this, ChatBot.class);
        startActivity(intent);
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://drauziovarella.uol.com.br/videos/coluna/tdah-a-importancia-do-tratamento-durante-a-infancia-e-a-vida-adulta/";
        } else if (id == R.id.btnMaterial2) {
            url = "https://draanabeatriz.com.br/mentes-inquietas-tdah-desatencao-hiperatividade-e-impulsividade/";
        } else if (id == R.id.btnMaterial3) {
            url = "https://drauziovarella.uol.com.br/pediatria/tdah-transtorno-do-deficit-de-atencao-com-hiperatividade/";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}