package com.example.cf;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TelaCPF extends AppCompatActivity {

    private EditText cpfEditText;
    private Button verificarButton;
    private Socket socket;

    {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{"websocket", "polling"};
            opts.reconnection = true;
            opts.forceNew = true;
            socket = IO.socket("https://d3e958883217.ngrok-free.app", opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onCpfValid = args -> runOnUiThread(() -> {
        if (args.length > 0 && args[0] instanceof JSONObject) {
            JSONObject data = (JSONObject) args[0];
            String nome = data.optString("nome", data.optString("name", "Desconhecido"));
            String cpf = data.optString("cpf", "CPF não disponível");
            Toast.makeText(TelaCPF.this, "CPF válido! Usuário: " + nome + " CPF: " + cpf, Toast.LENGTH_SHORT).show();
            // Você pode navegar para a próxima tela aqui
            Informations.CPF = cpf;
            Informations.nome = nome;

            Intent intent = new Intent(TelaCPF.this, Chat.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(TelaCPF.this, "Resposta inválida do servidor", Toast.LENGTH_SHORT).show();
        }
    });

    private final Emitter.Listener onCpfInvalid = args -> runOnUiThread(() -> {
        if (args.length > 0 && args[0] instanceof JSONObject) {
            JSONObject data = (JSONObject) args[0];
            String erro = data.optString("erro", "CPF não encontrado.");
            Toast.makeText(TelaCPF.this, erro, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TelaCPF.this, "CPF não encontrado.", Toast.LENGTH_SHORT).show();
        }
    });

    private final Emitter.Listener onConnectionError = args -> runOnUiThread(() -> {
        String erro = args.length > 0 ? args[0].toString() : "Erro desconhecido";
        Log.e("SOCKET", "Erro de conexão: " + erro);
        Toast.makeText(TelaCPF.this, "Erro de conexão com o servidor: " + erro, Toast.LENGTH_LONG).show();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cpf);

        cpfEditText = findViewById(R.id.cpf);
        verificarButton = findViewById(R.id.button);

        socket.on("cpfValid", onCpfValid);
        socket.on("cpfInvalid", onCpfInvalid);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);

        socket.connect();

        verificarButton.setOnClickListener(v -> verificar());
    }

    private void verificar() {
        String cpf = cpfEditText.getText().toString().trim();

        if (cpf.isEmpty() || cpf.length() < 11) {
            Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("cpf", cpf);
            json.put("tipo", Informations.tipo);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao criar JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        socket.emit("joinChat", json);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("cpfValid", onCpfValid);
        socket.off("cpfInvalid", onCpfInvalid);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectionError);
    }
}