package com.mobio.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CombineView extends RelativeLayout {

    public CombineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.custom_combine_view, this);
        ImageView imageView = findViewById(R.id.image);
        TextView textView = findViewById(R.id.caption);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CombineView);
        int type = attributes.getInt(R.styleable.CombineView_type, 0);
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.CombineView_image));
        textView.setText(attributes.getString(R.styleable.CombineView_text));
        attributes.recycle();
    }
}
