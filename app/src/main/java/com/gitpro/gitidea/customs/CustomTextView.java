package com.gitpro.gitidea.customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.gitpro.gitidea.R;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(@NonNull Context context) {
        super(context);
        initCustomText(context,null);
    }

    public CustomTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCustomText(context,attrs);
    }

    public CustomTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomText(context,attrs);
    }
    private void initCustomText(Context context, AttributeSet  attributeSet) {
     if (attributeSet!=null) {
         TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTextView);
         String fontName = typedArray.getString(R.styleable.CustomTextView_typeface);

         try {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" +fontName);
             setTypeface(typeface);
         } catch (Exception e) {
           e.printStackTrace();
         }
         typedArray.recycle();
     }
    }
}
