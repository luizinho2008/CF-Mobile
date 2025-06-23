package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChatBot extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
    }

    public void telainicial(View v) {
        Intent intent = new Intent(ChatBot.this, TelaInicial.class);
        startActivity(intent);
    }
}