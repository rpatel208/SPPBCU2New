package com.allentownblower.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.allentownblower.R;
import com.allentownblower.common.Utility;
import com.allentownblower.module.MultipleSelection;

import java.util.ArrayList;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class MultipleSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<MultipleSelection> multipleSelections;
    private Activity act;

    public MultipleSelectionAdapter(Activity act,  ArrayList<MultipleSelection> multipleSelections) {
        this.act = act;
        this.multipleSelections = multipleSelections;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate your layout and pass it to view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_multipleselection_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MultipleSelection object = multipleSelections.get(position);

        ((ItemHolder) holder).txt_MultipleSelection_Option.setText(object.getName());

       if(object.isSelected()){
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((ItemHolder) holder).txt_MultipleSelection_Option.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
            } else {
                ((ItemHolder) holder).txt_MultipleSelection_Option.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
            }
           ((ItemHolder) holder).txt_MultipleSelection_Option.setTextColor(act.getResources().getColor(R.color.white));
       } else {
           final int sdk = android.os.Build.VERSION.SDK_INT;
           if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
               ((ItemHolder) holder).txt_MultipleSelection_Option.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.wifi_sub_setting_screen_box));
           } else {
               ((ItemHolder) holder).txt_MultipleSelection_Option.setBackground(ContextCompat.getDrawable(act, R.drawable.wifi_sub_setting_screen_box));
           }
           ((ItemHolder) holder).txt_MultipleSelection_Option.setTextColor(act.getResources().getColor(R.color.blwrdetl_dark_blue));
       }

        ((ItemHolder) holder).txt_MultipleSelection_Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0 ; i < multipleSelections.size(); i++){
                    if(multipleSelections.get(i).getId().equals(object.getId())){
                        multipleSelections.get(i).setSelected(true);
                        Utility.Log("TAG","Selection => "+object.getId());
                    } else {
                        multipleSelections.get(i).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return multipleSelections.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView txt_MultipleSelection_Option;

        public ItemHolder(View itemView) {
            super(itemView);
            txt_MultipleSelection_Option = itemView.findViewById(R.id.txt_MultipleSelection_Option);
        }
    }
}