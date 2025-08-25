package com.example.cf;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import org.json.JSONObject;


public class CadastroActivity extends AppCompatActivity {

    EditText editNome, editEmail, editSenha;
    Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Liberar rede na thread principal (somente para testes)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        btnCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            // Aplica o hash na senha
            String senhaHash = gerarHashSHA256(senha);

            // Envia para o backend
            enviarCadastro(nome, email, senhaHash);
        });

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Função que gera hash SHA-256 da senha
    private String gerarHashSHA256(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(senha.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return senha; // fallback
        }
    }

    private void enviarCadastro(String nome, String email, String senhaHash) {
        try {
            // Troque pela URL real do seu backend
            URL url = new URL("https://b96a93478c31.ngrok-free.app/cadPost");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON com senha em hash
            String jsonInputString = String.format(
                    "{\"nome\": \"%s\", \"email\": \"%s\", \"senha\": \"%s\"}",
                    nome, email, senhaHash
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Cadastro enviado com sucesso!", Toast.LENGTH_SHORT).show());
            } else {
                // Leitura do corpo da resposta de erro
                InputStream errorStream = conn.getErrorStream();
                StringBuilder errorMessage = new StringBuilder();

                if (errorStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorMessage.append(line.trim());
                    }
                    reader.close();
                }

                String finalErrorMessage = errorMessage.toString();

                runOnUiThread(() -> {
                    String msg = "Erro ao cadastrar: ";

                    if (responseCode == 409 || finalErrorMessage.toLowerCase().contains("email")) {
                        msg += "Email já cadastrado.";
                    } else {
                        msg += responseCode + " - " + finalErrorMessage;
                    }

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                });
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}