package com.example.cf;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CadastroActivity extends AppCompatActivity {

    EditText editNome, editEmail, editSenha;
    Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Permitir rede na thread principal (somente para testes)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        btnCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            // Envia senha crua para o backend gerar hash
            enviarCadastro(nome, email, senha);
        });

        TextView login = findViewById(R.id.signUp);
        login.setOnClickListener(v -> startActivity(new Intent(CadastroActivity.this, LoginActivity.class)));
    }

    private void enviarCadastro(String nome, String email, String senha) {
        try {
            URL url = new URL(Informations.link + "/cadPost");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"nome\": \"%s\", \"email\": \"%s\", \"senha\": \"%s\"}",
                    nome, email, senha
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream responseStream = (responseCode == HttpURLConnection.HTTP_OK) ?
                    conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line.trim());
            reader.close();

            String finalResponse = response.toString();

            runOnUiThread(() -> {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "Erro ao cadastrar: " + finalResponse, Toast.LENGTH_LONG).show();
                }
            });

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}