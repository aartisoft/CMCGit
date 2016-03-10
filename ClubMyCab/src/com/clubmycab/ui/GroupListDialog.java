package com.clubmycab.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;

import com.clubmycab.R;
import com.clubmycab.model.ContactData;

/**
 * Created by newpc on 7/3/16.
 */
public class GroupListDialog extends Dialog{
    private Context context;

    public GroupListDialog(Context context) {
        super(context);
        this.context = context;

    }

    private void intViwew(ArrayList<ContactData> list){
        setContentView(R.layout.dialog_grouplist);



    }


}
