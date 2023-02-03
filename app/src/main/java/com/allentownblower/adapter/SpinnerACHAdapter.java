package com.allentownblower.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allentownblower.R;
import com.allentownblower.module.SpinnerObject;

import java.util.ArrayList;

/**
 * Created by appcode1 on 8/12/15.
 */
public class SpinnerACHAdapter extends ArrayAdapter<SpinnerObject> {


    private Activity act;
    private ArrayList<SpinnerObject> arrACH = new ArrayList<>();

    public SpinnerACHAdapter(Activity act, ArrayList<SpinnerObject> arrACH) {
        super(act, R.layout.row_spinner, arrACH);
        this.act = act;
        this.arrACH = arrACH;
        arrACH.add(0, new SpinnerObject("Choose ACH Value"));
    }

    private class ViewHolder {
        protected TextView txtLang;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = act.getLayoutInflater().inflate(R.layout.row_spinner, null);
            holder.txtLang = v.findViewById(R.id.txt_row_Spinner);

//            holder.txtLang.setBackgroundColor(act.getResources().getColor(android.R.color.white));
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.txtLang.setText(""+ arrACH.get(position).getName());
        return v;
    }
}
