package com.example.newbike.view;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class MoblieEditText extends EditText {
    public MoblieEditText(Context context) {
        super(context);
        this.addTextChangedListener(new MobileWatcher());
    }

    public MoblieEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(new MobileWatcher());
    }

    public MoblieEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addTextChangedListener(new MobileWatcher());
    }

    class MobileWatcher implements TextWatcher {
        int beforeTextLength = 0;
        int onTextLength = 0;
        boolean isChanged = false;
        int location = 0;// 记录光标的位置
        private char[] tempChar;
        private final StringBuffer buffer = new StringBuffer();
        int konggeNumberB = 0;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeTextLength = s.length();
            if (buffer.length() > 0) {
                buffer.delete(0, buffer.length());
            }
            konggeNumberB = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ' ') {
                    konggeNumberB++;
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextLength = s.length();
            buffer.append(s.toString());
            if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                isChanged = false;
                return;
            }
            isChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isChanged) {
                location = getSelectionEnd();
                int index = 0;
                while (index < buffer.length()) {
                    if (buffer.charAt(index) == ' ') {
                        buffer.deleteCharAt(index);
                    } else {
                        index++;
                    }
                }
                index = 0;
                int konggeNumberC = 0;
                while (index < buffer.length()) {
                    if ((index == 3 || index == 8)) {
                        buffer.insert(index, ' ');
                        konggeNumberC++;
                    }
                    index++;
                }
                if (konggeNumberC > konggeNumberB) {
                    location += (konggeNumberC - konggeNumberB);
                }
                tempChar = new char[buffer.length()];
                buffer.getChars(0, buffer.length(), tempChar, 0);
                String str = buffer.toString();
                if (location > str.length()) {
                    location = str.length();
                } else if (location < 0) {
                    location = 0;
                }
                setText(str);
                Editable etable = getText();
                Selection.setSelection(etable, location);
                isChanged = false;
            }
        }
    }
}
