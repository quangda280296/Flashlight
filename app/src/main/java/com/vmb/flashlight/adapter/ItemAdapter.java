package com.vmb.flashlight.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vmb.flashlight.base.holder.BaseViewHolder;

import com.flash.light.bright.R;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public ItemAdapter(Context context) {
        this.context = context;
    }

    protected int getResLayout() {
        return R.layout.row_option;
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(this.context == null)
            return null;

        LayoutInflater inflater = LayoutInflater.from(this.context);
        return new BaseViewHolder(inflater.inflate(getResLayout(), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((BaseViewHolder) viewHolder).bindData(position);
    }
}
