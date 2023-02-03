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
public class SpinnerSupplyAdapter extends ArrayAdapter<SpinnerObject> {


    Activity act;
    ArrayList<SpinnerObject> arrSupply = new ArrayList<>();

    public SpinnerSupplyAdapter(Activity act, ArrayList<SpinnerObject> arrSupply) {
        super(act, R.layout.row_spinner, arrSupply);
        this.act = act;
        this.arrSupply = arrSupply;
//        arrLang.add(0, new SpinnerObject("Choose Language", 0));
    }

    private class ViewHolder {
        protected TextView txtPolarity;
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
            holder.txtPolarity = v.findViewById(R.id.txt_row_Spinner);

//            holder.txtLang.setBackgroundColor(act.getResources().getColor(android.R.color.white));
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.txtPolarity.setText(arrSupply.get(position).getName());
        return v;
    }
}
