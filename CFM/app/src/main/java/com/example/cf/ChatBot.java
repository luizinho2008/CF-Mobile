package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatBot extends AppCompatActivity {

    private static final String TAG = "ChatBot";

    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter adapter;
    private List<String> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        adapter = new MessageAdapter(messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(adapter);

        // Permite requisições de rede na thread principal (apenas para teste rápido)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Adiciona a mensagem do usuário na tela
                messages.add("Você: " + userMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);

                editTextMessage.setText("");

                // Envia para o backend Node.js
                new Thread(() -> sendMessageToBackend(userMessage)).start();
            }
        });
    }

    public void telainicial(View v) {
        Intent intent = new Intent(ChatBot.this, TelaInicial.class);
        startActivity(intent);
    }

    private void sendMessageToBackend(String texto) {
        try {
            // Monta a URL dinamicamente usando o número do chat
            String backendUrl = Informations.link + "/sendMessage" + Informations.number;
            URL url = new URL(backendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Cria sessionId único para cada chat
            String sessionId = "chat" + Informations.number + "_" + System.currentTimeMillis();

            // Monta JSON para enviar
            JSONObject json = new JSONObject();
            json.put("message", texto);
            json.put("sessionId", sessionId);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            // Lê resposta
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            conn.disconnect();

            JSONObject responseJson = new JSONObject(sb.toString());
            String botReply = responseJson.optString("reply", "[sem resposta do bot]");

            // Atualiza interface
            runOnUiThread(() -> {
                messages.add("Bot: " + botReply);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            });

        } catch (Exception e) {
            Log.e(TAG, "Erro ao enviar mensagem para backend: ", e);
            runOnUiThread(() -> {
                messages.add("Erro ao enviar mensagem: " + e.getMessage());
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            });
        }
    }

    // Adapter interno para RecyclerView
    private static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

        private final List<String> messages;

        public MessageAdapter(List<String> messages) {
            this.messages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            holder.textMessage.setText(messages.get(position));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView textMessage;

            public MessageViewHolder(View itemView) {
                super(itemView);
                textMessage = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}