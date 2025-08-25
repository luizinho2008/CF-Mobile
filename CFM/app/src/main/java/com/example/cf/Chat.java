package com.example.cf;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Chat extends AppCompatActivity {

    private LinearLayout messagesLayout;
    private ScrollView scrollView;
    private Socket socket;
    private TextView loadingTextView;

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

    // Listener para carregar mensagens anteriores (histórico)
    private final Emitter.Listener onPreviousMessages = args -> runOnUiThread(() -> {
        messagesLayout.removeAllViews();  // Remove tudo, inclusive a mensagem de carregando

        if (args.length > 0 && args[0] instanceof JSONArray) {
            JSONArray messages = (JSONArray) args[0];

            if (messages.length() == 0) {
                addEmptyMessageView("Nenhuma mensagem ainda. Envie a primeira!");
            } else {
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject msg = messages.optJSONObject(i);
                    if (msg == null) continue;

                    String nome = msg.optString("nome", "Desconhecido");
                    String cpf = msg.optString("cpf", "");
                    String mensagem = msg.optString("message", "");

                    boolean isLogado = cpf.equals(Informations.CPF);
                    addMessageToLayout(nome, mensagem, isLogado);
                }
            }
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    });

    // Listener para nova mensagem em tempo real
    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        if (args.length > 0 && args[0] instanceof JSONObject) {
            JSONObject msg = (JSONObject) args[0];

            String nome = msg.optString("nome", "Desconhecido");
            String cpf = msg.optString("cpf", "");
            String mensagem = msg.optString("message", "");

            boolean isLogado = cpf.equals(Informations.CPF);

            // Remove a mensagem de "nenhuma mensagem ainda" se existir
            removeEmptyMessageView();

            addMessageToLayout(nome, mensagem, isLogado);

            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    });

    private void addEmptyMessageView(String text) {
        TextView emptyView = new TextView(this);
        emptyView.setText(text);
        emptyView.setTextColor(Color.BLACK);
        emptyView.setTextSize(24);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 40, 20, 40);
        emptyView.setLayoutParams(params);
        messagesLayout.addView(emptyView);
    }

    private void removeEmptyMessageView() {
        for (int i = 0; i < messagesLayout.getChildCount(); i++) {
            if (messagesLayout.getChildAt(i) instanceof TextView) {
                TextView tv = (TextView) messagesLayout.getChildAt(i);
                if (tv.getText().toString().contains("Nenhuma mensagem ainda")) {
                    messagesLayout.removeView(tv);
                    break;
                }
            }
        }
    }

    private void addMessageToLayout(String nome, String mensagem, boolean isLogado) {
        TextView textView = new TextView(this);
        textView.setText(nome + "\n\n" + mensagem);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setPadding(20, 16, 20, 16);

        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(24);
        background.setColor(isLogado ? Color.BLUE : generateColorFromName(nome));

        textView.setBackground(background);

        int metadeDaTela = getResources().getDisplayMetrics().widthPixels / 2;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                metadeDaTela,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 10, 20, 10);
        params.gravity = isLogado ? Gravity.END : Gravity.START;

        textView.setLayoutParams(params);
        messagesLayout.addView(textView);
    }

    private int generateColorFromName(String name) {
        int hash = name.hashCode();
        Random rand = new Random(hash);
        int r = 100 + rand.nextInt(156);
        int g = 100 + rand.nextInt(156);
        int b = 100 + rand.nextInt(156);
        return Color.rgb(r, g, b);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView logadoTextView = findViewById(R.id.logado);
        logadoTextView.setText("Olá, " + Informations.nome + "!");

        scrollView = findViewById(R.id.scroll_view);
        messagesLayout = findViewById(R.id.messages_layout);

        // Mensagem de carregamento
        loadingTextView = new TextView(this);
        loadingTextView.setText("Carregando mensagens...");
        loadingTextView.setTextColor(Color.BLACK);
        loadingTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        loadingTextView.setTextSize(24);
        loadingTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 40, 20, 40);
        loadingTextView.setLayoutParams(params);
        messagesLayout.addView(loadingTextView);

        // Escuta eventos
        socket.on("previousMessages", onPreviousMessages);
        socket.on("newMessage", onNewMessage);
        socket.connect();

        // Envia dados de join
        JSONObject joinData = new JSONObject();
        try {
            joinData.put("cpf", Informations.CPF);
            joinData.put("tipo", Informations.tipo);
            socket.emit("joinChat", joinData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Envio de mensagem
        EditText messageInput = findViewById(R.id.message_input);
        Button sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> {
            String mensagem = messageInput.getText().toString().trim();
            if (!mensagem.isEmpty()) {
                JSONObject msgObj = new JSONObject();
                try {
                    msgObj.put("nome", Informations.nome);
                    msgObj.put("cpf", Informations.CPF);
                    msgObj.put("message", mensagem);

                    // Remove a mensagem de "nenhuma mensagem ainda" antes de enviar
                    removeEmptyMessageView();

                    socket.emit("sendMessage", msgObj);
                    messageInput.setText(""); // limpa campo após envio
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void voltar(View view) {
        Intent intent = new Intent(Chat.this, TelaCPF.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("previousMessages", onPreviousMessages);
        socket.off("newMessage", onNewMessage);
    }
}