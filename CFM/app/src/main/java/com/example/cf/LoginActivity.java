package com.example.cf;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editSenha;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextSenha);
        btnLogin = findViewById(R.id.btnLogin);

        // Permitir rede na thread principal (somente para testes)
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();
            enviarLogin(email, senha);
        });

        TextView signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));
    }

    private void enviarLogin(String email, String senha) {
        try {
            URL url = new URL(Informations.link + "/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"email\": \"%s\", \"senha\": \"%s\"}", email, senha);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream responseStream = (responseCode == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line.trim());
            reader.close();

            String finalResponse = response.toString();

            runOnUiThread(() -> {
                try {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        JSONObject json = new JSONObject(finalResponse);
                        JSONObject user = json.getJSONObject("user");

                        Informations.ID = user.getString("id");
                        Informations.usuario = user.getString("nome");
                        Informations.email = user.getString("email");

                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, TelaInicial.class));
                    } else {
                        Toast.makeText(this, "Erro no login: " + finalResponse, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao processar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}