package com.example.yaroslav.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] pattern = {3, 4, 7, 1};
        setContentView(R.layout.activity_main);
        CreditCardEditText cardEditText = (CreditCardEditText) findViewById(R.id.card_view);
        if (cardEditText != null) {
            cardEditText.setTextPattern(pattern);
        }

    }
}
