package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private List<Message> messages = new ArrayList<>();

    // 🔑 Mantém a sessão fixa durante a conversa
    private String sessionId;

    // Classe para representar uma mensagem
    private static class Message {
        String text;
        String sender; // "user" ou "bot"

        public Message(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }
    }

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // 🔑 Define sessionId fixo para o usuário logado
        sessionId = "chat_" + Informations.number;

        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                messages.add(new Message(userMessage, "user"));
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);

                editTextMessage.setText("");

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
            String backendUrl = Informations.link + "/sendMessage" + Informations.number;
            URL url = new URL(backendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("message", texto);
            json.put("sessionId", sessionId); // usa o sessionId fixo

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

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

            runOnUiThread(() -> {
                messages.add(new Message(botReply, "bot"));
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            });

        } catch (Exception e) {
            Log.e(TAG, "Erro ao enviar mensagem para backend: ", e);
            runOnUiThread(() -> {
                messages.add(new Message("Erro: " + e.getMessage(), "bot"));
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            });
        }
    }

    private static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private final List<Message> messages;
        private static final int MESSAGE_COLOR = Color.parseColor("#002EFF");

        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
            return new MessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);

            String prefix;
            if (message.sender.equals("user")) {
                prefix = "Você: ";
                holder.textMessage.setGravity(Gravity.END);
            } else { // Bot
                prefix = "Bot: ";
                holder.textMessage.setGravity(Gravity.START);
            }

            holder.textMessage.setText(prefix + message.text);
            holder.textMessage.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.textMessage.setTextColor(MESSAGE_COLOR);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView textMessage;

            public MessageViewHolder(View itemView) {
                super(itemView);
                textMessage = itemView.findViewById(R.id.textMessage);
            }
        }
    }
}