package me.shetj.base.view;

import android.content.Context;
import androidx.annotation.Keep;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Toast;

import me.shetj.base.tools.file.CharactorHandler;


@Keep
public class ContainsEmojiEditText extends AppCompatEditText {
    //输入表情前的光标位置  
    private int cursorPos;
    //输入表情前EditText中的文本  
    private String inputAfterText;
    //是否重置了EditText的内容  
    private boolean resetText;

    private Context mContext;

    public ContainsEmojiEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    private void initEditText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    cursorPos = getSelectionEnd();
                    inputAfterText= s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count-before >= 2) {
                        CharSequence input = s.subSequence(start+before, start + count);
                        if (CharactorHandler.containsEmoji(input.toString())) {
                            resetText = true;
                            Toast.makeText(mContext, "请不要使用特殊符号表情！", Toast.LENGTH_SHORT).show();
                            setText(inputAfterText);
                            CharSequence text = getText();
                            if (text != null) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}