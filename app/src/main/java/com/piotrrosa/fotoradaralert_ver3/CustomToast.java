package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by piotr on 01.02.15.
 */
public class CustomToast {
    public static void show(CharSequence content, Context context) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context,content,duration);
        toast.show();
    }
}
