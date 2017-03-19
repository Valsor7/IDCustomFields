package com.example.yaroslav.myapplication;

import android.util.Log;
import android.view.View;

public interface EmiratesId extends MyEditText.FilledLengthFilter.OnTextViewFilledListener{

    void setOnTextChangedListener(CreditCardEditText.OnTextChangedListener onTextChangedListener);

    void setCardNumber(String cardNumber);

    void setTextPattern(int[] pattern);

    boolean isFilled();

    String getFilledTextWithDashes();

    String getFilledTextWtihoutDashes();

    void showKeyboard();
}
