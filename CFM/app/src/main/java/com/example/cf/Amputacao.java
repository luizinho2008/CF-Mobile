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

public class Amputacao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_amputacao);
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
            url = "https://bionicenter.com.br/2020/10/09/tipos-de-amputacoes-causas-e-niveis-de-amputacao/#:~:text=A%20amputa%C3%A7%C3%A3o%20%C3%A9%20a%20remo%C3%A7%C3%A3o,cirurgia%20preventiva%20para%20esses%20problemas.";
        } else if (id == R.id.btnMaterial2) {
            url = "https://hcfmb.unesp.br/wp-content/uploads/2019/06/Amputado-1.pdf";
        } else if (id == R.id.btnMaterial3) {
            url = "https://repositorio.londrina.pr.gov.br/index.php/menu-educacao/educacao-e-a-covid-19/inclusao/31471-material-apoio-deficiencia-fisica/file";
        }

        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
}