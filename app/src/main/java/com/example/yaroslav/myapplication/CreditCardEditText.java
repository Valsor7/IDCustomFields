package com.example.yaroslav.myapplication;


import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class CreditCardEditText extends LinearLayout implements EmiratesId {

    private static final String TAG = "CreditCardEditText";
    private static int mLayoutMargin;
    private static int mEditTextPadding;

    private List<MyEditText> mEditTextList;
    private int[] mPattern;

    private int mCurrentPosition = 0;
    private int mTempPosition = 0;
    private int mCharCountToBeFilled = -1;
    private boolean isClearPrevious = false;
    private EmiratesId mEmiratesId;

    private OnTextChangedListener mOnTextChangedListener;

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange: has focus " + hasFocus);
            MyEditText editText = (MyEditText) v;
            if (hasFocus) {
                if (editText != null) {
                    editText.setSelection(editText.getText().length());
                    mCurrentPosition = mEditTextList.indexOf(editText);
                    Log.d(TAG, "onFocusChange: pos " + mCurrentPosition);
                }
            } else {
                if (editText != null) {
                    editText.clearFocus();
                }
            }
        }
    };
    private boolean isEnglish = true;


    public CreditCardEditText(Context context) {
        super(context);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mEditTextList = new ArrayList<>();
        if (isEnglish){
            mEmiratesId = new EmiratesIdLTR(getContext(), mEditTextList);
        } else {
            mEmiratesId = new EmiratesIdRTL();
        }

        setOrientation(HORIZONTAL);
        setLayoutDirection(LAYOUT_DIRECTION_LTR);
        mLayoutMargin = dpToPx(3);
        mEditTextPadding = 0;
    }

    @Override
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        mEmiratesId.setOnTextChangedListener(onTextChangedListener);
    }

    @Override
    public void setCardNumber(String cardNumber) {
        mEmiratesId.setCardNumber(cardNumber);
    }

    @Override
    public void setTextPattern(int[] pattern) {
        mEmiratesId.setTextPattern(pattern);
    }

    private int getPatternSumm() {
        int patternSumm = 0;
        for (int i = 0; i < mPattern.length; i++) {
            patternSumm += mPattern[i];
        }
        return patternSumm;
    }

    private int getCurrentCharCount() {
        int currentCharCount = 0;
        for (MyEditText editText : mEditTextList) {
            currentCharCount += editText.getText().length();
        }
        return currentCharCount;
    }

    @Override
    public boolean isFilled() {
        int currentCharCount = getCurrentCharCount();
        return mCharCountToBeFilled == currentCharCount;
    }

    @Override
    public String getFilledTextWithDashes() {
        StringBuilder sb = new StringBuilder();
        for (MyEditText editText : mEditTextList) {
            sb.append("-");
            sb.append(editText.getText());
        }
        sb.deleteCharAt(0);

        Log.d(TAG, "getFilledTextWithDashes: " + sb);
        return sb.toString();
    }

    @Override
    public String getFilledTextWtihoutDashes() {
        StringBuilder sb = new StringBuilder();
        for (MyEditText editText : mEditTextList) {
            sb.append(editText.getText());
        }
        return sb.toString();
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void showKeyboard() {

    }

    @Override
    public void onFilled() {
        mEmiratesId.onFilled();
    }

    public interface OnTextChangedListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }
}