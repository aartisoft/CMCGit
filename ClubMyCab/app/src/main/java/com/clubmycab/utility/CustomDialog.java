package com.clubmycab.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by newpc on 4/5/16.
 */
public class CustomDialog {
    public static void showDialog(final Context context, String  message){
        AlertDialog alertDialog = new AlertDialog.Builder(
               context).create();
        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }
}
