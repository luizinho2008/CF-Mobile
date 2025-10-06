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

public class Autismo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autismo);
    }

    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "neurodivergentes";
    }

    public void chatbot(View v) {
        Informations.number = "1";
        Intent intent = new Intent(Autismo.this, ChatBot.class);
        startActivity(intent);
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://www.scielo.br/j/pee/a/NwnK5kF4zM9m9XRynr53nwF/?format=html&lang=pt";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.abraac.org/";
        } else if (id == R.id.btnMaterial3) {
            url = "https://www.youtube.com/watch?v=OvCyEbY7Mog";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}