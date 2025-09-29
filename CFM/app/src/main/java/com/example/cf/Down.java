package com.example.cf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Down extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_down);
    }

    // Botão para abrir o fórum
    public void irForum(View view) {
        startActivity(new Intent(this, Chat.class));
        Informations.tipo = "intelectuais";
    }

    // Método único para abrir links externos
    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.btnMaterial1) {
            url = "https://bvsms.saude.gov.br/21-3-dia-mundial-e-dia-nacional-da-sindrome-de-down-o-que-significa-inclusao/";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.amazon.com.br/S%C3%ADndrome-Down-as-pr%C3%A1ticas-pedag%C3%B3gicas/dp/8532651895";
        } else if (id == R.id.btnMaterial3) {
            url = "https://www.movimentodown.org.br/sindrome-de-down/o-que-e/";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}