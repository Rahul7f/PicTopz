package com.example.pictopz.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.pictopz.R;

public class ProgressDialog {

    private Context context;
    private AlertDialog dialog;
    private TextView textMsg;


    public ProgressDialog(Context context) {
        this.context=context;
        dialog=new AlertDialog.Builder(context).create();

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.progress_dialog,null);
        textMsg = view.findViewById(R.id.progress_dialog_msg);
        dialog.setView(view);
        dialog.setCancelable(false);
    }

    public void setText(String msg) {
        textMsg.setText(msg);
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }
    public void showDialog(){
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }

}
