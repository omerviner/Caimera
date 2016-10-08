package com.example.viner.erosion;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
class ViewHolderColumn extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row

    public ImageButton img1;
    public ImageButton img2;
    public ImageButton img3;
    public ImageButton img4;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolderColumn(final View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        img1 = (ImageButton)itemView.findViewById(R.id.imgBtn1);
        img2 = (ImageButton)itemView.findViewById(R.id.imgBtn2);
        img3 = (ImageButton)itemView.findViewById(R.id.imgBtn3);
        img4 = (ImageButton)itemView.findViewById(R.id.imgBtn4);
    }
}
