package com.example.cf;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{"websocket", "polling"};
            opts.reconnection = true;
            opts.forceNew = true;
            socket = IO.socket("https://conexaofamiliar.onrender.com/", opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onPreviousMessages = args -> runOnUiThread(() -> {
        if (args.length > 0 && args[0] instanceof JSONArray) {
            JSONArray messages = (JSONArray) args[0];
            for (int i = 0; i < messages.length(); i++) {
                JSONObject msg = messages.optJSONObject(i);
                if (msg == null) continue;

                String nome = msg.optString("nome", "Desconhecido");
                String cpf = msg.optString("cpf", "");
                String mensagem = msg.optString("message", "");

                boolean isLogado = cpf.equals(Informations.CPF);
                addMessageToLayout(nome, mensagem, isLogado);
            }
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    });

    private void addMessageToLayout(String nome, String mensagem, boolean isLogado) {
        TextView textView = new TextView(this);
        // Nome e mensagem separados por linha
        textView.setText(nome + "\n\n" + mensagem);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setPadding(20, 16, 20, 16);

        // Criar fundo arredondado
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(24); // arredondamento em px
        if (isLogado) {
            background.setColor(Color.parseColor("blue")); // cor para usuário logado
        } else {
            background.setColor(generateColorFromName(nome));
        }
        textView.setBackground(background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if (isLogado) {
            params.gravity = Gravity.END;
        } else {
            params.gravity = Gravity.START;
        }

        params.setMargins(20, 10, 20, 10);
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

        socket.on("previousMessages", onPreviousMessages);
        socket.connect();

        // Emitir para carregar mensagens da sala correta
        JSONObject joinData = new JSONObject();
        try {
            joinData.put("cpf", Informations.CPF);
            joinData.put("tipo", Informations.tipo);
            socket.emit("joinChat", joinData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("previousMessages", onPreviousMessages);
    }
}