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

public class Williams extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_williams);
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
            url = "https://www.mackenzie.br/fileadmin/ARQUIVOS/Public/6-pos-graduacao/upm-higienopolis/mestrado-doutorado/disturbios_desenvolvimento/2018/periodicos/Manejo_comportamental_de_crian%C3%A7as_e_adolescentes_com_S%C3%ADndrome_de_Williams.pdf";
        } else if (id == R.id.btnMaterial2) {
            url = "https://www.swbrasil.org.br/";
        } else if (id == R.id.btnMaterial3) {
            url = "https://fiocruz.br/biosseguranca/Bis/infantil/sindrome-willians.htm";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}