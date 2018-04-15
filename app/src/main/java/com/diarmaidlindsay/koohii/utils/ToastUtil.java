package com.diarmaidlindsay.koohii.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.diarmaidlindsay.koohii.R;

/**
 * Replacement for Android Toast class so I can programmatically apply themes
 */
public class ToastUtil {
    public static Toast makeText(Context context, String text, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Activity activity = (Activity) context;
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_layout));

        // set a message
        TextView textView = (TextView) layout.findViewById(R.id.toast_text);
        textView.setText(text);

        // Toast...
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        return toast;
    }

    public static Toast makeText(Context context, int resId, int duration) {
        return makeText(context, context.getResources().getText(resId).toString(), duration);
    }
}
