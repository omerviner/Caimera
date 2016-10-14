package com.example.viner.erosion;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

/*
    ChooseActivity ViewHolder (4 Images in a column)
 */
class ViewHolderColumn extends RecyclerView.ViewHolder {
    public final int COULMN_SIZE = 4;
    public ImageButton[] imageButtons = new ImageButton[COULMN_SIZE];

    public ViewHolderColumn(final View itemView) {
        super(itemView);
        imageButtons[0] = (ImageButton)itemView.findViewById(R.id.imgBtn1);
        imageButtons[1] = (ImageButton)itemView.findViewById(R.id.imgBtn2);
        imageButtons[2] = (ImageButton)itemView.findViewById(R.id.imgBtn3);
        imageButtons[3] = (ImageButton)itemView.findViewById(R.id.imgBtn4);
    }
}
