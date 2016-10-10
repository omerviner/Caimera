package com.example.viner.erosion;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
class ViewHolderColumn extends RecyclerView.ViewHolder {
    public final int COULMN_SIZE = 4;
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public ImageButton[] imageButtons = new ImageButton[COULMN_SIZE];


    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolderColumn(final View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);
        imageButtons[0] = (ImageButton)itemView.findViewById(R.id.imgBtn1);
        imageButtons[1] = (ImageButton)itemView.findViewById(R.id.imgBtn2);
        imageButtons[2] = (ImageButton)itemView.findViewById(R.id.imgBtn3);
        imageButtons[3] = (ImageButton)itemView.findViewById(R.id.imgBtn4);
    }
}
