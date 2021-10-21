package com.mobio.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ComboText extends RelativeLayout {
    private TextView title;
    private TextView content;
    public ComboText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.custom_combo_text, this);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ComboText);
        setTextContent(attributes.getString(R.styleable.ComboText_textContent));
        setTextTitle(attributes.getString(R.styleable.ComboText_textTitle));
        attributes.recycle();
    }

    public void setTextContent(String str){
        content.setText(str);
    }

    public void setTextTitle(String str){
        title.setText(str);
    }
}
