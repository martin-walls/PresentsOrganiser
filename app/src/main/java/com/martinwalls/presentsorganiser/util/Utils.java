package com.martinwalls.presentsorganiser.util;

import android.content.Context;
import android.util.TypedValue;

public class Utils {

    private Utils() {}

    public static int convertDpToPixelSize(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }
}
