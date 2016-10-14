package com.example.viner.erosion;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

/*
    EffectsActivity RecyclerView ViewHolder
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageButton img;
    public ImageButton caimera_sign;


    public ViewHolder(final View itemView) {
        super(itemView);

        caimera_sign = (ImageButton)itemView.findViewById(R.id.caimera_sign);
        img = (ImageButton)itemView.findViewById(R.id.imgBtn);

    }
}
