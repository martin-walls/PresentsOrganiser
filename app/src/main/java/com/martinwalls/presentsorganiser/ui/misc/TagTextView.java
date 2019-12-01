package com.martinwalls.presentsorganiser.ui.misc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.martinwalls.presentsorganiser.R;
import com.martinwalls.presentsorganiser.util.Utils;

public class TagTextView extends AppCompatTextView {

    private final int CORNER_RADIUS_DP = 4;
    private final int TEXT_SIZE = 11;
    private final int PAD_HORIZONTAL_DEFAULT = 4;
    private final int PAD_VERTICAL_DEFAULT = 1;

    @ColorInt
    private int tagColor;

    int paddingVertical;
    int paddingHorizontal;

    int cornerRadius;

    public TagTextView(Context context) {
        super(context);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagTextView);

        tagColor = a.getColor(R.styleable.TagTextView_tagColor,
                getResources().getColor(R.color.colorPrimary, context.getTheme()));


        paddingVertical = a.getDimensionPixelSize(R.styleable.TagTextView_tagPaddingVertical,
                Utils.convertDpToPixelSize(context, PAD_VERTICAL_DEFAULT));
        paddingHorizontal = a.getDimensionPixelSize(R.styleable.TagTextView_tagPaddingHorizontal,
                Utils.convertDpToPixelSize(context, PAD_HORIZONTAL_DEFAULT));

        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        cornerRadius = Utils.convertDpToPixelSize(context, CORNER_RADIUS_DP);

        setTextColor(Color.WHITE);
        setAllCaps(true);
        setTextSize(TEXT_SIZE);
        setTypeface(Typeface.DEFAULT_BOLD);
        setLetterSpacing(0.05f);
    }

    public void setTagColor(@ColorInt int tagColor) {
        this.tagColor = tagColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = getPaint();
        paint.setColor(tagColor);

        int top = 0;
        int left = 0;
        int right = getWidth();
        int bottom = getHeight();

//        canvas.drawRect(left, top, right, bottom, paint);
        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, paint);

        super.onDraw(canvas);
    }
}
