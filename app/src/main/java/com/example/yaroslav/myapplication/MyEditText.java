package com.example.yaroslav.myapplication;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MyEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "MyEditText";

    private List<InputFilter> mInputFilters = new ArrayList<>();

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addInputFilter(InputFilter inputFilter){
        mInputFilters.add(inputFilter);
        setFilters(mInputFilters.toArray(new InputFilter[0]));
    }

    public static class FilledLengthFilter extends InputFilter.LengthFilter {
        private static final String TAG = "FilledLengthFilter";

        public interface OnTextViewFilledListener {
            public void onFilled();
        }

        private EditText mEditText;
        private OnTextViewFilledListener mFilledListener;
        private int mMax;

        public FilledLengthFilter(int max, EditText editText, OnTextViewFilledListener listener) {
            super(max);
            mEditText = editText;
            mFilledListener = listener;
            mMax = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Log.d(TAG, "filter() called with: source = [" + source + "], start = [" + start + "], end = [" + end + "], dest = [" + dest + "], dstart = [" + dstart + "], dend = [" + dend + "]");
            //// TODO: 19-Mar-17 bug when field is full and del btn is pressed instead of number

            if (mMax == dest.length() && !TextUtils.isEmpty(source)) {
                mFilledListener.onFilled();
            }
            return super.filter(source, start, end, dest, dstart, dend);
        }


    }
}
