package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatBot extends AppCompatActivity {

    private static final String TAG = "ChatBot";
    // *** ALTERADO AQUI: O PROJECT_ID AGORA APONTA PARA ONDE O AUTISMBOT ESTÁ ***
    private static final String PROJECT_ID = "deficienciasbot-ravi";

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

        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            Log.d(TAG, "Usuário enviou mensagem: " + userMessage);
            if (!userMessage.isEmpty()) {
                messages.add("Você: " + userMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerViewMessages.scrollToPosition(messages.size() - 1);

                editTextMessage.setText("");

                new Thread(() -> {
                    try {
                        Log.d(TAG, "Enviando mensagem para Dialogflow...");
                        String response = sendMessage(userMessage);
                        Log.d(TAG, "Resposta recebida do Dialogflow: " + response);
                        final String finalResponse = (response == null || response.trim().isEmpty()) ?
                                "[sem resposta do bot]" : response;
                        if (response == null || response.trim().isEmpty()) {
                            Log.w(TAG, "Resposta do Dialogflow está vazia.");
                        }
                        runOnUiThread(() -> {
                            messages.add("Bot: " + finalResponse);
                            adapter.notifyItemInserted(messages.size() - 1);
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao enviar mensagem para Dialogflow: ", e);
                        runOnUiThread(() -> {
                            messages.add("Erro ao enviar mensagem: " + e.getMessage());
                            adapter.notifyItemInserted(messages.size() - 1);
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);
                        });
                    }
                }).start();
            } else {
                Log.d(TAG, "Mensagem do usuário vazia, não envia.");
            }
        });
    }

    public void telainicial(View v) {
        Intent intent = new Intent(ChatBot.this, TelaInicial.class);
        startActivity(intent);
    }

    private String sendMessage(String texto) throws Exception {
        Log.d(TAG, "Iniciando sendMessage com texto: " + texto);
        InputStream stream = getResources().openRawResource(R.raw.dialogflow_credentials);
        GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
        SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
        settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials));

        SessionsClient sessionsClient = SessionsClient.create(settingsBuilder.build());
        SessionName session = SessionName.of(PROJECT_ID, "123456");

        TextInput.Builder textInput = TextInput.newBuilder().setText(texto).setLanguageCode("pt-BR");
        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build();

        DetectIntentResponse response = sessionsClient.detectIntent(request);
        QueryResult queryResult = response.getQueryResult();
        Log.d(TAG, "QueryResult obtido: " + queryResult);
        sessionsClient.close();

        return queryResult.getFulfillmentText();
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