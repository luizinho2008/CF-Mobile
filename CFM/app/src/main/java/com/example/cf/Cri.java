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

public class Cri extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cri);
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
            url = "https://jornal.usp.br/atualidades/sindrome-de-cri-du-chat-deve-ser-investigada-nas-primeiras-horas-de-vida/#:~:text=A%20s%C3%ADndrome%20de%20cri%2Ddu,resulta%20em%20uma%20anomalia%20gen%C3%A9tica.&text=Conhecida%20como%20%E2%80%9Cmiado%20de%20gato,som%20do%20choro%20do%20beb%C3%AA";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.youtube.com/watch?v=OcCPc5g24wg";
        } else if (id == R.id.btnMaterial3) {
            url = "https://criduchatbrasil.com/sobre-a-sindrome";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}