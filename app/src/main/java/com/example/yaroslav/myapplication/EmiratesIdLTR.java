package com.example.yaroslav.myapplication;


import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
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

public class EmiratesIdLTR implements EmiratesId {
    private static final String TAG = "CreditCardEditText";
    private static int mLayoutMargin;
    private static int mEditTextPadding;

    private List<MyEditText> mEditTextList;
    private int[] mPattern;

    private int mCurrentPosition = 0;

    private int mTempPosition = 0;
    private int mCharCountToBeFilled = -1;
    private boolean isClearPrevious = false;
    private Context mContext;
    private CreditCardEditText.OnTextChangedListener mOnTextChangedListener;

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
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


    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.d(TAG, "onKey: " + keyCode);
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return true;
            }
            if (event.getKeyCode() != KeyEvent.KEYCODE_DEL) {
                return false;
            }
            EditText editText = ((EditText) v);
            Log.d(TAG, "onKey: end cursor " + editText.getSelectionEnd());
//            if (TextUtils.isEmpty(editText.getText())) {
//                Log.d(TAG, "onKey: empty field");
//                if (isClearPrevious) {
//                    clearPreviousLastNumber();
//                }
//            } else
            if (editText.getSelectionEnd() == 0) {
                Log.d(TAG, "onKey: selection == 0");
                clearPreviousLastNumber();
            }
            Log.d(TAG, "onKey: finish");
            return false;
        }
    };

    public EmiratesIdLTR(Context context, List<MyEditText> editTextList) {
        this.mEditTextList = editTextList;
        this.mContext = context;
    }

    @Override
    public void setOnTextChangedListener(CreditCardEditText.OnTextChangedListener onTextChangedListener) {
        mOnTextChangedListener = onTextChangedListener;
    }

    @Override
    public void setCardNumber(String cardNumber) {
        int startPosition = 0;

        for (MyEditText editText : mEditTextList) {
            int endPosition = editText.getMaxEms() + startPosition;

            if (endPosition >= cardNumber.length()) {
                endPosition = cardNumber.length();
            }

            String actualNumber = cardNumber.substring(startPosition, endPosition);
            editText.setText(actualNumber);
            startPosition = endPosition;
            editText.setSelection(actualNumber.length());

            if (startPosition == cardNumber.length()) {
                return;
            }
        }
    }

    @Override
    public void setTextPattern(int[] pattern) {
        mPattern = pattern;
        mCharCountToBeFilled = getPatternSumm();
        for (int number : pattern) {
            addEditText(number);
        }
        setFocusOnFirst();
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

    public MyEditText addEditText(int length) {
        MyEditText editText = new MyEditText(mContext);
        editText.setMaxEms(length);
        editText.setLongClickable(false);
        editText.setImeOptions(EditorInfo.IME_ACTION_NONE);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        editText.setBackgroundResource(R.drawable.edit_text_background);
        editText.setOnFocusChangeListener(mOnFocusChangeListener);
        editText.addTextChangedListener(new CreditCardTextWatcher(length));
        editText.setOnKeyListener(mOnKeyListener);
        editText.setPadding(mEditTextPadding, mEditTextPadding, mEditTextPadding, mEditTextPadding);
        editText.addInputFilter(new MyEditText.FilledLengthFilter(length, editText, this));


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, (float) length);
        layoutParams.setMargins(mLayoutMargin, mLayoutMargin, mLayoutMargin, mLayoutMargin);
        editText.setLayoutParams(layoutParams);
        mEditTextList.add(editText);
        return editText;
    }

    @Override
    void setFocusOnFirst() {
        if (mEditTextList.size() > 0) {
            Log.d(TAG, "setFocusOnFirst: ");
            mEditTextList.get(0).requestFocus();
        }
    }

    private void onFocusNext() {
        if (mCurrentPosition < mEditTextList.size() - 1) {
            mCurrentPosition++;
            Log.d(TAG, "onFocusNext: mCurrentPosition " + mCurrentPosition);
            requestCurrentFocus();
        } else {
            mCurrentPosition = mTempPosition;
        }
    }

    private void clearPreviousLastNumber() {
        if (mCurrentPosition > 0) {
            Log.d(TAG, "clearPreviousLastNumber: current position  " + mCurrentPosition);
            mCurrentPosition--;
            Log.d(TAG, "clearPreviousLastNumber: previous position  " + mCurrentPosition);
            MyEditText mCurrentEditText = mEditTextList.get(mCurrentPosition);
            if (!TextUtils.isEmpty(mCurrentEditText.getText())) {
                Editable text = mCurrentEditText.getText();
                text.delete(text.length() - 1, text.length());
            }
            requestCurrentFocus();
        }
    }

    private void requestCurrentFocus() {
        MyEditText mCurrent = mEditTextList.get(mCurrentPosition);
        if (mCurrent.getText().length() == mPattern[mCurrentPosition]) {
            onFocusNext();
        } else {
            mCurrent.requestFocus();
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public void showKeyboard() {
        if (!mEditTextList.isEmpty()) {
            setFocusOnFirst();
            Log.d(TAG, "showKeyboard: ");
            //UIUtil.showKeyboard(getContext(), mEditTextList.get(0));
        }
    }

    @Override
    public void onFilled() {
        Log.d(TAG, "onFilled: ");
        onFocusNext();
    }

    class CreditCardTextWatcher implements TextWatcher {
        private int mLengthLimit;

        CreditCardTextWatcher(int lengthLimit) {
            mLengthLimit = lengthLimit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mOnTextChangedListener != null) {
                mOnTextChangedListener.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isClearPrevious = false;
            mTempPosition = mCurrentPosition;
            int length = s.length();
            Log.d(TAG, "onTextChanged: " + length);

            if (length == mLengthLimit) {
                onFocusNext();
            }

//            if (length == 0) {
//                if (mCurrentPosition >= 1) {
//                    mCurrentPosition--;
//                }
//                requestCurrentFocus();
//            }

            if (mOnTextChangedListener != null) {
                mOnTextChangedListener.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mOnTextChangedListener != null) {
                mOnTextChangedListener.afterTextChanged(s);
            }
        }
    }

    public interface OnTextChangedListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    public int getmCurrentPosition() {
        return mCurrentPosition;
    }

    public void setmCurrentPosition(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    public int getmTempPosition() {
        return mTempPosition;
    }

    public void setmTempPosition(int mTempPosition) {
        this.mTempPosition = mTempPosition;
    }
}